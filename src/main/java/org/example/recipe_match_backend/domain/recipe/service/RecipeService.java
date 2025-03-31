package org.example.recipe_match_backend.domain.recipe.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.ingredient.domain.Ingredient;
import org.example.recipe_match_backend.domain.ingredient.repository.IngredientRepository;
import org.example.recipe_match_backend.domain.recipe.domain.*;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeIngredientDto;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeStepDto;
import org.example.recipe_match_backend.domain.recipe.dto.mapping.IngredientJsonDTO;
import org.example.recipe_match_backend.domain.recipe.dto.mapping.RecipeJsonDTO;
import org.example.recipe_match_backend.domain.recipe.dto.mapping.RecipeStepJsonDTO;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeSortRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeUpdateRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeIdAndUserUidResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeSaveResponse;
import org.example.recipe_match_backend.domain.recipe.repository.*;
import org.example.recipe_match_backend.domain.tool.domain.Tool;
import org.example.recipe_match_backend.domain.tool.repository.ToolRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.type.AllergyType;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ToolRepository toolRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeToolRepository recipeToolRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeBookMarkRepository recipeBookMarkRepository;
    private final AmazonS3Client amazonS3Client;
    private final RecipeImageRepository recipeImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public void loadRecipesFromJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath);

        List<RecipeJsonDTO> recipeDTOList = objectMapper.readValue(
                jsonFile,
                new TypeReference<List<RecipeJsonDTO>>() {}
        );

        for (RecipeJsonDTO dto : recipeDTOList) {

            CategoryType category = parseCategoryWithDefault(dto.getCategory());
            DifficultyType difficulty = DifficultyType.fromKorName(dto.getDifficulty());

            List<AllergyType> allergyList = new ArrayList<>();
            if (dto.getAllergy() != null && !dto.getAllergy().trim().isEmpty()) {
                String[] tokens = dto.getAllergy().split(",");
                for (String token : tokens) {
                    String trimmed = token.trim();
                    try {
                        allergyList.add(AllergyType.fromDisplayName(trimmed));
                    } catch (Exception e) {
                        log.error("알레르기 리스트 추가 실패했습니다.");
                    }
                }
            }

            Recipe recipe = Recipe.builder()
                    .recipeName(dto.getRecipeName())
                    .description(dto.getDescription())
                    .cookingTime(dto.getCookingTime())
                    .alterTools(dto.getAlterTools())
                    .difficulty(difficulty)
                    .category(category)
                    .allergies(allergyList)
                    .build();

            // 재료 처리
            for (IngredientJsonDTO ingDto : dto.getIngredients()) {
                Ingredient ingredient = ingredientRepository
                        .findByIngredientName(ingDto.getIngredientName())
                        .orElseGet(() -> {
                            Ingredient newIng = Ingredient.builder()
                                    .ingredientName(ingDto.getIngredientName())
                                    .build();
                            ingredientRepository.save(newIng);
                            return newIng;
                        });

                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                        .ingredient(ingredient)
                        .quantity(ingDto.getQuantity())
                        .build();

                recipe.addRecipeIngredient(recipeIngredient);
                ingredient.addRecipeIngredient(recipeIngredient);
            }

            // 도구 처리
            for (String toolName : dto.getTools()) {
                Tool tool = toolRepository
                        .findByToolName(toolName)
                        .orElseGet(() -> {
                            Tool newTool = Tool.builder()
                                    .toolName(toolName)
                                    .build();
                            toolRepository.save(newTool);
                            return newTool;
                        });

                RecipeTool recipeTool = RecipeTool.builder()
                        .tool(tool)
                        .build();

                recipe.addRecipeTool(recipeTool);
                tool.addRecipeTool(recipeTool);
            }

            // 단계 처리
            for (RecipeStepJsonDTO stepDto : dto.getSteps()) {
                RecipeStep step = RecipeStep.builder()
                        .stepOrder(stepDto.getStepOrder())
                        .content(stepDto.getContent())
                        .build();
                recipe.addRecipeStep(step);
            }

            double cookingTime = recipe.getCookingTime();
            double stepSize = recipe.getRecipeSteps().size();
            double ingredientSize = recipe.getRecipeIngredients().size();
            double toolSize = recipe.getRecipeTools().size();

            recipeDifficulty(recipe, cookingTime, stepSize, ingredientSize, toolSize);

            recipeRepository.save(recipe);
        }
    }

    @Transactional
    public RecipeSaveResponse save(RecipeRequest request) throws IOException {
        // 사용자 조회
        User user = userRepository.findByUid(request.getUserUid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Recipe 엔티티 생성
        Recipe recipe = Recipe.builder()
                .recipeName(request.getRecipeName())
                .description(request.getDescription())
                .cookingTime(request.getCookingTime())
                .category(request.getCategory())
                .recipeIngredients(new ArrayList<>())
                .recipeSteps(new ArrayList<>())
                .recipeTools(new ArrayList<>())
                .recipeImages(new ArrayList<>())
                .user(user)
                .build();

        // 사용자와 레시피 관계 설정
        user.addRecipe(recipe);

        // Ingredients 처리
        for (RecipeIngredientDto ingredientDto : request.getRecipeIngredientDtos()) {
            // 기존 Ingredient 조회 또는 새로 생성
            Ingredient ingredient = ingredientRepository.findByIngredientName(ingredientDto.getIngredientName())
                    .orElseGet(() -> {
                        Ingredient newIngredient = Ingredient.builder()
                                .ingredientName(ingredientDto.getIngredientName())
                                .recipeIngredients(new ArrayList<>())
                                .userIngredients(new ArrayList<>())
                                .build();
                        return ingredientRepository.save(newIngredient);
                    });

            // RecipeIngredient 생성
            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .quantity(ingredientDto.getQuantity())
                    .ingredient(ingredient)
                    .build();

            // 양방향 관계 설정
            recipe.addRecipeIngredient(recipeIngredient);
            ingredient.addRecipeIngredient(recipeIngredient);
        }


        // Tools 처리
        for (String toolName : request.getToolName()) {
            Tool tool = toolRepository.findByToolName(toolName)
                    .orElseGet(() -> {
                        Tool newTool = Tool.builder()
                                .toolName(toolName)
                                .recipeTools(new ArrayList<>())
                                .userTools(new ArrayList<>())
                                .build();
                        return toolRepository.save(newTool);
                    });

            // RecipeTool 생성
            RecipeTool recipeTool = RecipeTool.builder()
                    .tool(tool)
                    .build();

            // 양방향 관계 설정
            recipe.addRecipeTool(recipeTool);
            tool.addRecipeTool(recipeTool);
        }

        // RecipeSteps 처리
        for (RecipeStepDto stepDto : request.getRecipeStepDtos()) {
            RecipeStep step = RecipeStep.builder()
                    .stepOrder(stepDto.getStepOrder())
                    .content(stepDto.getContent())
                    .build();
            recipe.addRecipeStep(step);
        }

        recipeDifficulty(recipe, recipe.getCookingTime(), recipe.getRecipeSteps().size(),recipe.getRecipeIngredients().size() , recipe.getRecipeTools().size());

        /**
        if(request.getFiles() == null){
            String url = getDefaultImageUrl(recipe.getCategory());
            RecipeImage img = RecipeImage.builder()
                    .token(url)//그럼 이거는?
                    .build();
            recipe.addRecipeImage(img);
        }else{
            for(MultipartFile file:request.getFiles()){
                String token = uploadFile(file, recipe.getId());
                RecipeImage img = RecipeImage.builder()
                        .token(token)//바로 찾는건 불가능 하니 key값을 저장하자
                        .build();
                recipe.addRecipeImage(img);
            }
        }
        **/
        for(MultipartFile file:request.getFiles()){
            String token = uploadFile(file, recipe.getId());
            RecipeImage img = RecipeImage.builder()
                    .token(token)//바로 찾는건 불가능 하니 key값을 저장하자
                    .build();
            recipe.addRecipeImage(img);
        }
        // Recipe 저장 (CascadeType.PERSIST에 의해 연관된 엔티티들도 함께 저장됨)
        Recipe savedRecipe = recipeRepository.save(recipe);

        return new RecipeSaveResponse(recipe.getAlternativeTool(), recipe.getAllergies(), request.getUserUid(), recipe.getId());
    }

    @Transactional
    public RecipeSaveResponse update(Long recipeId, RecipeUpdateRequest request) throws IOException {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 레시피가 존재하지 않습니다."));

        // === 1) 레시피의 기존 정보(도구, 재료, 단계, 이미지 등) 전부 삭제 ===
        // 1-1) 기존 RecipeTool 제거
        for (RecipeTool recipeTool : new ArrayList<>(recipe.getRecipeTools())) {
            // 양방향 관계 끊기
            recipeTool.getTool().getRecipeTools().remove(recipeTool);
            recipe.getRecipeTools().remove(recipeTool);
        }

        // 1-2) 기존 RecipeIngredient 제거
        for (RecipeIngredient recipeIngredient : new ArrayList<>(recipe.getRecipeIngredients())) {
            // 양방향 관계 끊기
            recipeIngredient.getIngredient().getRecipeIngredients().remove(recipeIngredient);
            recipe.getRecipeIngredients().remove(recipeIngredient);
        }

        // 1-3) 기존 RecipeStep 제거
        for (RecipeStep step : new ArrayList<>(recipe.getRecipeSteps())) {
            recipe.getRecipeSteps().remove(step);
        }

        // 1-4) 기존 RecipeImage 제거 & S3 객체도 삭제
        for (RecipeImage recipeImage : new ArrayList<>(recipe.getRecipeImages())) {
            amazonS3Client.deleteObject(bucketName, recipeImage.getToken());
            recipe.getRecipeImages().remove(recipeImage);
        }

        // === 2) Request로부터 새롭게 들어온 정보 반영 ===
        // 2-1) 레시피 메타 정보 업데이트
        if (request.getRecipeName() != null) {
            recipe.setRecipeName(request.getRecipeName());
        }
        if (request.getCategory() != null) {
            recipe.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            recipe.setDescription(request.getDescription());
        }
        if (request.getCookingTime() != null) {
            recipe.setCookingTime(request.getCookingTime());
        }

        // 2-2) Tools 추가
        if (request.getToolName() != null) {
            for (String toolName : request.getToolName()) {
                Tool tool = toolRepository.findByToolName(toolName)
                        .orElseGet(() -> {
                            Tool newTool = Tool.builder()
                                    .toolName(toolName)
                                    .recipeTools(new ArrayList<>())
                                    .userTools(new ArrayList<>())
                                    .build();
                            return toolRepository.save(newTool);
                        });

                RecipeTool recipeTool = RecipeTool.builder()
                        .tool(tool)
                        .build();

                // 양방향 관계 설정
                recipe.addRecipeTool(recipeTool);
                tool.addRecipeTool(recipeTool);
            }
        }

        // 2-3) Ingredients 추가
        if (request.getRecipeIngredientDtos() != null) {
            for (RecipeIngredientDto dto : request.getRecipeIngredientDtos()) {
                Ingredient ingredient = ingredientRepository.findByIngredientName(dto.getIngredientName())
                        .orElseGet(() -> {
                            Ingredient newIngredient = Ingredient.builder()
                                    .ingredientName(dto.getIngredientName())
                                    .recipeIngredients(new ArrayList<>())
                                    .userIngredients(new ArrayList<>())
                                    .build();
                            return ingredientRepository.save(newIngredient);
                        });

                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                        .quantity(dto.getQuantity())
                        .ingredient(ingredient)
                        .build();

                recipe.addRecipeIngredient(recipeIngredient);
                ingredient.addRecipeIngredient(recipeIngredient);
            }
        }

        // 2-4) Steps 추가
        if (request.getRecipeStepDtos() != null) {
            for (RecipeStepDto stepDto : request.getRecipeStepDtos()) {
                RecipeStep step = RecipeStep.builder()
                        .stepOrder(stepDto.getStepOrder())
                        .content(stepDto.getContent())
                        .build();
                recipe.addRecipeStep(step);
            }
        }

        // 2-5) 새 이미지들 추가
        if (request.getFiles() != null) {
            for (MultipartFile file : request.getFiles()) {
                String token = uploadFile(file, recipe.getId());
                RecipeImage img = RecipeImage.builder()
                        .token(token)
                        .build();
                recipe.addRecipeImage(img);
            }
        }

        // === 3) 난이도 계산 로직 (예시 그대로) ===
        recipeDifficulty(recipe,
                recipe.getCookingTime(),
                recipe.getRecipeSteps().size(),
                recipe.getRecipeIngredients().size(),
                recipe.getRecipeTools().size());

        // === 4) 최종 응답 ===
        return new RecipeSaveResponse(recipe.getAlternativeTool(), recipe.getAllergies(), request.getUserUid(), recipe.getId());
    }


    @Transactional
    public void delete(Long recipeId){
        Recipe recipe = recipeRepository.findById(recipeId).get();
        for(RecipeImage recipeImage:recipe.getRecipeImages()){
            amazonS3Client.deleteObject(bucketName,recipeImage.getToken());
        }
        recipeRepository.deleteById(recipeId);
    }

    public RecipeResponse find(Long recipeId,String uid){
        Recipe recipe = recipeRepository.findById(recipeId).get();
        User user = userRepository.findByUid(uid).get();

        Boolean recipeLike = recipeLikeRepository.findByUserAndRecipe(user,recipe).isPresent();
        Boolean recipeBookMark = recipeBookMarkRepository.findByUserAndRecipe(user, recipe).isPresent();

        int likeSize = recipeLikeRepository.findByRecipe(recipe).size();
        int bookMarkSize = recipeBookMarkRepository.findByRecipe(recipe).size();

        List<String> urls = new ArrayList<>();
        for(RecipeImage recipeImage:recipe.getRecipeImages()){
            urls.add(""+amazonS3Client.getUrl(bucketName, recipeImage.getToken()));
        }

        return new RecipeResponse(recipe,recipeLike,likeSize,recipeBookMark,bookMarkSize,urls);
    }

    public List<RecipeResponse> findAll(){
        List<Recipe> recipes = recipeRepository.findAll();
        List<RecipeResponse> recipeResponses = new ArrayList<>();
        for(Recipe recipe:recipes){
            int likeSize = recipeLikeRepository.findByRecipe(recipe).size();
            int bookMarkSize = recipeBookMarkRepository.findByRecipe(recipe).size();

            List<String> urls = new ArrayList<>();
            for(RecipeImage recipeImage:recipe.getRecipeImages()){
                urls.add(""+amazonS3Client.getUrl(bucketName, recipeImage.getToken()));
            }
            recipeResponses.add(new RecipeResponse(recipe,likeSize,bookMarkSize,urls));
        }

        return recipeResponses;
    }

    public List<RecipeResponse> findByKeyword(String keyword){
        List<Recipe> recipes = recipeRepository.findByKeyword(keyword);
        List<RecipeResponse> recipeResponses = new ArrayList<>();
        for(Recipe recipe:recipes){
            int likeSize = recipeLikeRepository.findByRecipe(recipe).size();
            int bookMarkSize = recipeBookMarkRepository.findByRecipe(recipe).size();

            List<String> urls = new ArrayList<>();
            for(RecipeImage recipeImage:recipe.getRecipeImages()){
                urls.add(""+amazonS3Client.getUrl(bucketName, recipeImage.getToken()));
            }
            recipeResponses.add(new RecipeResponse(recipe,likeSize,bookMarkSize,urls));
        }

        return recipeResponses;
    }

    private void recipeDifficulty(Recipe recipe,double cookingTime, double stepSize, double ingredientSize, double toolSize){

        double time = 0.3*((cookingTime-5)/175);

        double step = 0.4*((stepSize - 1)/14);

        double ingredient = 0.2*((ingredientSize-1)/14);

        double tool = 0.1*((toolSize-1)/14);

        double point = 100*(time+step+ingredient+tool);

        if(0 <= point && point <= 33){
            recipe.setDifficulty(DifficultyType.EASY);
        } else if (34 <= point && point <= 66) {
            recipe.setDifficulty(DifficultyType.MIDDLE);
        } else if (67 <= point) {
            recipe.setDifficulty(DifficultyType.HARD);
        }
    }
    /**
     * 받은 이미지가 null인 경우 or 레시피 api를 통해 가져온 레시피 인 경우
     * 카테고리별 기본 이미지 경로 반환.
     */
    private String getDefaultImageUrl(CategoryType category) {
        switch (category) {
            case JAPANESE:
                return "/static/images/japanese.jpg";
            case CHINESE:
                return "/static/images/chinese.jpg";
            case KOREAN:
                return "/static/images/korean.jpg";
            case WESTERN:
                return "/static/images/western.jpg";
            default:
                return "/static/images/default.jpg";
        }
    }

    private String uploadFile(MultipartFile multipartFile, Long recipeId) throws IOException {
        File fileObject = convertMultiPartFileToFile(multipartFile).get();
        String fileName = createFileName(multipartFile.getOriginalFilename());
        String key =  Long.toString(recipeId)+"/"+fileName;
        amazonS3Client.putObject(new PutObjectRequest(bucketName, key, fileObject));
        fileObject.delete();
        return key;
    }

    private Optional<File> convertMultiPartFileToFile(MultipartFile multipartFile) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    /**
     파일명을 UUID로 변경. 뒤에 파일 확장자를 붙이기 위해 파일명을 파라미터로 받는다.
     */
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private CategoryType parseCategoryWithDefault(String categoryStr) {
        if (categoryStr == null || categoryStr.trim().isEmpty()) {
            return CategoryType.DEFAULT;
        }
        try {
            return CategoryType.valueOf(categoryStr);  // 예: "KOREAN", "CHINESE", ...
        } catch (IllegalArgumentException e) {
            return CategoryType.DEFAULT;
        }
    }

    public List<RecipeResponse> sortRecipes(RecipeSortRequest request) {
        return null;
    }
}


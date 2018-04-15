package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



/**
 * Created by jt on 6/13/17.
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
       System.err.println("I'm in the service");

      
        return  recipeRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String id) {


        return recipeRepository.findById(id);
    }

    @Override
    public Mono<RecipeCommand> findCommandById(String id) {

        
    	 return recipeRepository.findById(id)
                 .map(recipe -> {
                     RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);

                     recipeCommand.getIngredients().forEach(rc -> {
                         rc.setRecipeId(recipeCommand.getId());
                     });

                     return recipeCommand;
                 });
     
    }

    @Override
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
      
       System.err.println("Saved RecipeId:");
        return recipeRepository.save(recipeCommandToRecipe.convert(command))
                .map(recipeToRecipeCommand::convert);
    }

    @Override
    public Mono <Void>  deleteById(String idToDelete) {
        recipeRepository.deleteById(idToDelete);
        
        return Mono.empty();
    }
}

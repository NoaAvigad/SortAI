package com.projects.dubhacks.sortai;

import android.gesture.Prediction;

import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

/**
 * Created by jeaniewu on 16-10-15.
 */
public class Client {

    final String clientId = "";
    final String clientSecret = "";

    final ClarifaiClient client;

    public List<ClarifaiOutput<Concept>> predictionResults;

    // Initialise client for sending requests
    public Client(){
        client = new ClarifaiBuilder(clientId, clientSecret).buildSync();
        predictionResults = null;
    }

    // this method populate the predictionResults with default models
    public void predictWithGeneralModel(ClarifaiInput inputImage) {
        Model<?> model = client.getDefaultModels().generalModel();
        predictWithModel(inputImage, model);
    }

    // this method populate the predictionResults with default models
    public void predictWithCustomModel(ClarifaiInput inputImage, String modelId) {
        Model<?> model = client.getModelByID(modelId).executeSync().get();
        predictWithModel(inputImage, model);
    }

    private void predictWithModel(ClarifaiInput inputImage, Model model){
        ClarifaiResponse<List<ClarifaiOutput<Concept>>> response = model.asConceptModel() // You can also do client.getModelByID("id") to get custom models
                .predict()
                .withInputs(inputImage)
                .executeSync();

        // TODO: handle unsuccesful response
        if (response.isSuccessful()){
            predictionResults = response.get();
        }

    }
}

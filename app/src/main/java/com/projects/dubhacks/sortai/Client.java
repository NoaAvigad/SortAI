package com.projects.dubhacks.sortai;

import android.gesture.Prediction;
import android.os.AsyncTask;

import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import rx.Subscriber;
import rx.Observable;

/**
 * Created by jeaniewu on 16-10-15.
 */
public class Client {

    final String clientId = "_H4YkBs2yrR3UPk-q1wfmVc2M1p03LMEaTwdl_95";
    final String clientSecret = "N8V5QI-AAJEM2g_SYALnwbw5se9h6bXyO3CzsIgR";

    final ClarifaiClient client;

    // Initialise client for sending requests
    public Client(){
        client = new ClarifaiBuilder(clientId, clientSecret).buildSync();
    }

    // this method populate the predictionResults with default models
    public Observable<List<ClarifaiOutput<Concept>>> predictWithGeneralModel(ClarifaiInput inputImage) {
        Model<?> model = client.getDefaultModels().generalModel();
        return predictWithModel(inputImage, model);
    }

    // this method populate the predictionResults with default models
    public Observable<List<ClarifaiOutput<Concept>>> predictWithCustomModel(ClarifaiInput inputImage, String modelId) {
        Model<?> model = client.getModelByID(modelId).executeSync().get();
        return predictWithModel(inputImage, model);
    }

    private Observable<List<ClarifaiOutput<Concept>>> predictWithModel(final ClarifaiInput inputImage, final Model model){
        try {
            return Observable.create(new Observable.OnSubscribe<List<ClarifaiOutput<Concept>>>() {
                @Override
                public void call(final Subscriber<? super List<ClarifaiOutput<Concept>>> subscriber) {
                    ClarifaiResponse<List<ClarifaiOutput<Concept>>> response = model.asConceptModel() // You can also do client.getModelByID("id") to get custom models
                            .predict()
                            .withInputs(inputImage)
                            .executeSync();

                    // TODO: handle unsuccesful response
                    if (response.isSuccessful()) {
                        subscriber.onNext(response.get());
                        subscriber.onCompleted();
                    }else{
                        subscriber.onError(new Exception());
                    }


                }
            });

        }catch (Exception e) {
            throw e;
        }
    }
}

package bbox.com.todoapp.ui.detail;

import android.util.Log;
import androidx.lifecycle.*;
import bbox.com.todoapp.models.Todo;
import bbox.com.todoapp.repository.MainRepository;
import retrofit2.Call;

import javax.inject.Inject;

public class DetailViewModel extends ViewModel {

    private static final String LOG_TAG = "shaon_mvvm";

    MediatorLiveData<Todo> insertedLiveToDos = new MediatorLiveData();

    @Inject
    MainRepository mainRepository;


    @Inject
    public DetailViewModel() //need to inject repository
    {
        Log.d(LOG_TAG , "*************ViewModel cosntructor called-1...");
    }


    public void createNewTodoInServer(Todo todo) {

        final LiveData<Todo> source  =  LiveDataReactiveStreams.fromPublisher( mainRepository.postTodoToServer(todo));

        insertedLiveToDos.addSource(source, new Observer<Todo>() {
            @Override
            public void onChanged(Todo todos) {
                insertedLiveToDos.setValue(todos);
                insertedLiveToDos.removeSource(source);
            }});
    }


    public void updateTodoInServer(int id,Todo todo) {

        final LiveData<Todo> source  =  LiveDataReactiveStreams.fromPublisher( mainRepository.updateTodoInServer(id, todo));


        insertedLiveToDos.addSource(source, new Observer<Todo>() {
            @Override
            public void onChanged(Todo todos) {
                insertedLiveToDos.setValue(todos);
                insertedLiveToDos.removeSource(source);
            }});
    }


    public Call<Void> deleteTodoInServer(int id) {

        return mainRepository.deleteTodoInServer(id);
    }


    public LiveData<Todo> observeInsertedToDoFromServer()
    {
        return insertedLiveToDos;
    }




}
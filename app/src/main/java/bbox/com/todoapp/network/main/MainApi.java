package bbox.com.todoapp.network.main;

import bbox.com.todoapp.models.Todo;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.*;

import java.util.List;

public interface MainApi {

    // get Todos
     @GET("todos.json")
     Observable<List<Todo>> getTodos();
     //Flowable<List<Todo>> getTodos();

    // new Todos
    @POST("todos.json")
    Flowable<Todo> createTodos(@Body Todo objTodo);

    // update Todos
    @PATCH("todos/{id}.json")
    Flowable<Todo> updateTodos(@Path("id") int id, @Body Todo objTodo);

    // delete Todos
    @DELETE("todos/{id}.json")
    Call<Void> deleteTodo(@Path("id") int id);




}

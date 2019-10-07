package bbox.com.todoapp.di;

import bbox.com.todoapp.di.main.MainScope;
import bbox.com.todoapp.network.main.MainApi;
import bbox.com.todoapp.repository.MainRepository;
import bbox.com.todoapp.util.Constants;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Module
public class AppModule {

    @Singleton
    @Provides
    static Retrofit provideRetrofitInstance(){
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    @Provides
    static MainApi provideMainApi(Retrofit retrofit){
        return retrofit.create(MainApi.class);
    }



    @Singleton
    @Provides
    static MainRepository provideMainRepository(MainApi mainApi){
            return new MainRepository(mainApi, new SimpleDateFormat("yyyy-MM-dd"));

    }





}

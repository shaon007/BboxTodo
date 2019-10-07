package bbox.com.todoapp.di;



import bbox.com.todoapp.di.detail.DetailViewModelModule;
import bbox.com.todoapp.di.main.MainRepositoryModule;
import bbox.com.todoapp.di.main.MainScope;
import bbox.com.todoapp.di.main.MainViewModelModule;
import bbox.com.todoapp.ui.detail.DetailActivity;
import bbox.com.todoapp.ui.detail.DetailViewModel;
import bbox.com.todoapp.ui.main.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {


     @ContributesAndroidInjector(
             modules = { MainViewModelModule.class}
    )
     abstract MainActivity contributeMainActivity();



    @ContributesAndroidInjector(
            modules = {DetailViewModelModule.class}
    )
    abstract DetailActivity contributeDetailActivity();
}

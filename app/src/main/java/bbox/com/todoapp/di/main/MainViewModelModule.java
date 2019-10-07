package bbox.com.todoapp.di.main;

import androidx.lifecycle.ViewModel;
import bbox.com.todoapp.di.ViewModelKey;
import bbox.com.todoapp.ui.main.MainViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;


@Module
public abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel bindMainViewModel(MainViewModel viewModel);


}

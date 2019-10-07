package bbox.com.todoapp.di.detail;

import androidx.lifecycle.ViewModel;
import bbox.com.todoapp.di.ViewModelKey;
import bbox.com.todoapp.ui.detail.DetailViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class DetailViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel.class)
    public abstract ViewModel bindMainViewModel(DetailViewModel viewModel);
}

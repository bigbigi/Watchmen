package com.auto.watchmen.base;

/**
 * Created by dage on 2017/7/10.
 */
public interface TaskDetailContract {
    interface View extends BaseView<Presenter> {
        void updateView();
    }

    interface Presenter extends BasePresenter {
        void check();
    }

}

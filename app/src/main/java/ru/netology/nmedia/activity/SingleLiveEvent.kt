package ru.netology.nmedia.activity

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        require(!hasActiveObservers()) {
            error("Multiple observers registered but only one will be notified of changes.")
        }

        super.observe(owner) { t ->
            if (pending) {
                pending = false
                observer.onChanged(t)
            }
        }
    }

    override fun setValue(t: T?) {
        pending = true
        super.setValue(t)
    }
}
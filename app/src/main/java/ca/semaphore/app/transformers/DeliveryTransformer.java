/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * DeliveryTransformer.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.transformers;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.semaphore.app.R;
import ca.semaphore.app.models.Delivery;
import ca.semaphore.app.utils.CollectionUtils;
import ca.semaphore.app.viewmodels.DeliveryViewModel;

public class DeliveryTransformer {

    private static SimpleDateFormat simpleDateFormat;

    /**
     * Private constructor to prevent instantiation
     */
    private DeliveryTransformer() { }

    @NonNull
    public static List<DeliveryViewModel> toViewModels(@NonNull Context context,
                                                       @IntRange(from = 0) int startPosition,
                                                       @Nullable List<Delivery> dataModels) {
        List<DeliveryViewModel> viewModels = new ArrayList<>();
        if (CollectionUtils.isNonEmpty(dataModels)) {
            for (int i = 0; i < dataModels.size(); i++) {
                viewModels.add(toViewModel(context, i + startPosition, dataModels.get(i)));

            }
        }

        return viewModels;
    }

    @NonNull
    public static DeliveryViewModel toViewModel(@NonNull Context context,
                                                @IntRange(from = 0) int position,
                                                @Nullable Delivery dataModel) {

        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(context.getString(R.string.main_date_heading),
                                                    Locale.getDefault());
        }

        DeliveryViewModel viewModel = new DeliveryViewModel();
        viewModel.position = position;

        if (dataModel == null) {
            return viewModel;
        }

        Resources resources = context.getResources();

        int numItems = dataModel.getTotal();
        viewModel.mDateText = simpleDateFormat.format(new Date(dataModel.getTimestamp() * 1000));
        viewModel.mItemsText = resources.getQuantityString(R.plurals.main_items, numItems, numItems);
        if (dataModel.getLetters() > 0) {
            viewModel.mLettersText = resources.getQuantityString(R.plurals.main_letters,
                                                                 dataModel.getLetters(),
                                                                 dataModel.getLetters());
        }

        if (dataModel.getMagazines() > 0) {
            viewModel.mMagazineText = resources.getQuantityString(R.plurals.main_magazines,
                                                                  dataModel.getMagazines(),
                                                                  dataModel.getMagazines());
        }

        if (dataModel.getNewspapers() > 0) {
            viewModel.mNewspapersText = resources.getQuantityString(R.plurals.main_newspapers,
                                                                    dataModel.getNewspapers(),
                                                                    dataModel.getNewspapers());
        }

        if (dataModel.getParcels() > 0) {
            viewModel.mParcelsText = resources.getQuantityString(R.plurals.main_parcels,
                                                                 dataModel.getParcels(),
                                                                 dataModel.getParcels());
        }

        viewModel.numItems = dataModel.getTotal();
        viewModel.isCategorising = dataModel.isCategorising();

        return viewModel;
    }
}

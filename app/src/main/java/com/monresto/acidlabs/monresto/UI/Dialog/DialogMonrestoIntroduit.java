package com.monresto.acidlabs.monresto.UI.Dialog;

import android.app.Activity;
import android.app.Dialog;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.Model.PopUp;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DialogMonrestoIntroduit extends Dialog {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Activity activity;
    private Unbinder unbinder;
    private List<PopUp> popUpList;

    public DialogMonrestoIntroduit(Activity context) {
        super(context, R.style.DialogFullScreen);
        this.activity = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_monresto_introduit);
        unbinder = ButterKnife.bind(this);
        popUpList = new ArrayList<>();
        popUpList.add(new PopUp(activity.getResources().getDrawable(R.drawable.img_1), "Commandez et payez via l'appli", false));
        popUpList.add(new PopUp(activity.getResources().getDrawable(R.drawable.img_2), "Un acheteur équipé de gel désinfectant, prépare votre commande et la dépose pour récupération", false));
        popUpList.add(new PopUp(activity.getResources().getDrawable(R.drawable.img_3), "Le livreur récupère votre carton sans contact avec l'acheteur", false));
        popUpList.add(new PopUp(activity.getResources().getDrawable(R.drawable.img_4), "Le livreur dépose votre colis à l'endroit que vous lui avez indiqué", false));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        PopUpAdapter popUpAdapter = new PopUpAdapter(activity, popUpList);
        recyclerView.setAdapter(popUpAdapter);
    }

    @OnClick(R.id.view_root)
    void close() {
        dismiss();
    }
}

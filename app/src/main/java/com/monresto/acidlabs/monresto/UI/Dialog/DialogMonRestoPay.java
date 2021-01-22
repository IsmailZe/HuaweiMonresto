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

public class DialogMonRestoPay extends Dialog {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Activity activity;
    private Unbinder unbinder;
    private List<PopUp> popUpList;

    public DialogMonRestoPay(Activity context) {
        super(context, R.style.DialogFullScreen);
        this.activity = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_monresto_pay);
        unbinder = ButterKnife.bind(this);
        popUpList = new ArrayList<>();
        popUpList.add(new PopUp(activity.getResources().getDrawable(R.drawable.group_94), "Evitez tout contact lors du paiement", true));
        popUpList.add(new PopUp(activity.getResources().getDrawable(R.drawable.group_137), "Téléchargez Semsem Pay", true));
        popUpList.add(new PopUp(activity.getResources().getDrawable(R.drawable.group_84), "Vous pouvez donner le montant de votre choix au livreur qui s ' occupera de charger votre compte Semsem Pay", true));
        popUpList.add(new PopUp(activity.getResources().getDrawable(R.drawable.group_376), "Payez vos futures commandes par simple scan de QR code", true));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        PopUpAdapter popUpAdapter = new PopUpAdapter(activity, popUpList);
        recyclerView.setAdapter(popUpAdapter);
    }

    @OnClick(R.id.view_root)
    void close() {
        dismiss();
    }
}

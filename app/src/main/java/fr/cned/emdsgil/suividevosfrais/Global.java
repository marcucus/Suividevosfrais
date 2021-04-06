package fr.cned.emdsgil.suividevosfrais;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Map.*;
import java.util.*;

abstract class Global {

    // tableau d'informations mémorisées
    public static Hashtable<Integer, FraisMois> listFraisMois = new Hashtable<>();
    /* Retrait du type de l'Hashtable (Optimisation Android Studio)
     * Original : Typage explicit =
	 * public static Hashtable<Integer, FraisMois> listFraisMois = new Hashtable<Integer, FraisMois>();
	*/
    private static Hashtable<Integer, FraisMois> listFraisMoisMaj = new Hashtable<>();
    // fichier contenant les informations sérialisées
    public static final String filename = "save.fic";

    /**
     * Modification de l'affichage de la date (juste le mois et l'année, sans le jour)
     */
    public static void changeAfficheDate(DatePicker datePicker, boolean afficheJours) {
        try {
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), null);
                if (daySpinnerId != 0)
                {
                    View daySpinner = datePicker.findViewById(daySpinnerId);
                    if (!afficheJours)
                    {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            }
        } catch (SecurityException | IllegalArgumentException e) {
            Log.d("ERROR", e.getMessage());
        }
    }
    public static Hashtable<Integer, FraisMois> getListFraisMoisMaj() {
        return listFraisMoisMaj;
    }
    public static void deleteFraisHorsForfait(int fraisHfIndex, int monthIndex){
        listFraisMois.get(monthIndex).supprFraisHf(fraisHfIndex);
    }

    public static void UpdateFrais(Hashtable<Integer, FraisMois> updateTableHashTable){
        JSONArray updateTableJSONArray = new JSONArray();
        //Ajout de l'identifiant utilisateur
        updateTableJSONArray.put(Global.listFraisMois);

        //Parcours de la table de mise à jour
        for ( Hashtable.Entry<Integer, FraisMois> entry : updateTableHashTable.entrySet() ) {
            //Instance du tableau JSON
            JSONArray ficheFraisMoisJSONArray = new JSONArray();

            //Récupération de la valeur
            FraisMois value = entry.getValue();

            //Ajout du mois et de l'année au tableau
            ficheFraisMoisJSONArray.put(value.getAnnee());
            ficheFraisMoisJSONArray.put(value.getMois());

            //Ajout des frais forfaitisés au tableau
            ficheFraisMoisJSONArray.put(value.getEtape());
            ficheFraisMoisJSONArray.put(value.getNuitee());
            ficheFraisMoisJSONArray.put(value.getKm());
            ficheFraisMoisJSONArray.put(value.getRepas());

            //Ajout du cas de modification du tableau
            JSONArray listeFraisHFJSONArray = new JSONArray();

            //Ajout du tableau des frais hf du mois à la fiche de frais
            ficheFraisMoisJSONArray.put(listeFraisHFJSONArray);

            //Ajout de la fiche au tableau JSON
            updateTableJSONArray.put(ficheFraisMoisJSONArray);
        }
        Log.d("Opération", "Envoi des données du tableau de mis à jour vers le serveur mysql");
    }
}

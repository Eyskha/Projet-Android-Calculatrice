package ecl.calculatrice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    boolean lastEntryIsOperator = false;
    boolean lastEntryIsEqual = false;
    boolean lastEntryIsLeftPar = false;
    boolean lastEntryIsRightPar = false;
    int parOpened = 0;
    boolean commaEntered = false;
    Double[] nb = new Double[0]; int lengthNb = 0;
    String[] op = new String[0]; int lengthOp = 0;
    int[] indexOperatorScreen = new int[]{0};
    int[] indexParLeft = new int[0];
    int[] indexParRight = new int[0];

    // For options's PopUp
    Dialog optionsDialog;
    int nbChiffresApresVirgule = 2;
    boolean anglesEnDegres = false;
    String language = "Français";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        optionsDialog = new Dialog(this);

        // Reset button
        Button resetButton = (Button) findViewById(R.id.buttonreset);
        resetButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                OnClickReset(view);
                return false;
            }});
    }

    public void setAppLocale(String localeCode){
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        res.updateConfiguration(config,dm);
    }

    public String getTextButtonPressed (View view){
        Button buttonPressed = (Button) view;
        return buttonPressed.getText().toString();
    }

    public void setTextScreen (String str, boolean keep){
        TextView screen = (TextView) findViewById(R.id.CalculatorScreen);
        if (keep) screen.setText(screen.getText().toString() + str);
        else screen.setText(str);
    }

    public String getTextScreen(){
        TextView screen = (TextView) findViewById(R.id.CalculatorScreen);
        return screen.getText().toString();
    }

    public void OnClickDisplay (View view){
        String number = getTextButtonPressed(view);
        if(lastEntryIsEqual){ OnClickReset(view); lastEntryIsEqual = false;}
        if(!lastEntryIsRightPar) {
            switch (number) {
                case ".":
                    if (!commaEntered) {
                        setTextScreen(number, true);
                        lastEntryIsOperator = false;
                        lastEntryIsRightPar = false;
                        lastEntryIsEqual = false;
                        commaEntered = true;
                    }
                    break;
                default:
                    setTextScreen(number, true);
                    lastEntryIsOperator = false;
                    lastEntryIsRightPar = false;
                    lastEntryIsEqual = false;
            }
        }
    }

    public Double[] AddElementDoubleTab(Double[] tab, double a){
        Double[] nbBis = new Double[tab.length+1];
        for (int i = 0; i < tab.length; i++) nbBis[i] = tab[i];
        nbBis[tab.length] = a;
        return nbBis;
    }

    public String[] AddElementStringTab(String[] tab, String a){
        String[] opBis = new String[tab.length+1];
        for (int i = 0; i < tab.length; i++) opBis[i] = tab[i];
        opBis[tab.length] = a;
        return opBis;
    }

    public int[] AddElementIntTab(int[] tab, int a){
        int[] tabBis = new int[tab.length+1];
        for (int i = 0; i < tab.length; i++) tabBis[i] = tab[i];
        tabBis[tab.length] = a;
        return tabBis;
    }

    public Double[] RemoveElementDoubleTab(Double[] tab, int index){
        Double[] nbBis = new Double[tab.length-1];
        for(int i=0; i<index;i++) nbBis[i] = tab[i];
        for(int i=index; i<tab.length-1; i++) nbBis[i] = tab[i+1];
        return nbBis;
    }

    public String[] RemoveElementStringTab(String[] tab, int index){
        String[] opBis = new String[tab.length-1];
        for(int i=0; i<index;i++) opBis[i] = tab[i];
        for(int i=index; i<tab.length-1; i++) opBis[i] = tab[i+1];
        return opBis;
    }

    public int[] RemoveElementIntTab(int[] tab, int index){
        int[] tabBis = new int[tab.length-1];
        if(index!=0) for(int i=0; i<index;i++) tabBis[i] = tab[i];
        for(int i=index; i<tab.length-1; i++) tabBis[i] = tab[i+1];
        return tabBis;
    }

    public void OnCLickOperation (View view){
        String operator = getTextButtonPressed(view);
        boolean test = false;
        if(indexOperatorScreen[indexOperatorScreen.length-1]==getTextScreen().length()){
            switch(operator) {
                case "-": test = true;
                default:
            }
        }
        if(!lastEntryIsOperator) {
            lengthNb += 1;
            if (getTextScreen() == "" || getTextScreen() == ".") nb = AddElementDoubleTab(nb,0.);
            // If parenthesis
            int nbParRight=0;
            for(int i:indexParRight){if(i==lengthNb-1) nbParRight+=1;}
            if(lastEntryIsRightPar) nb= AddElementDoubleTab(nb,Double.parseDouble(getTextScreen().substring(indexOperatorScreen[indexOperatorScreen.length-2],getTextScreen().length()-nbParRight)));
            else nb = AddElementDoubleTab(nb,Double.parseDouble(getTextScreen().substring(indexOperatorScreen[indexOperatorScreen.length-1])));

            lengthOp += 1;

            op = AddElementStringTab(op,operator);

            setTextScreen(operator, true);
            lastEntryIsOperator = true;
            lastEntryIsRightPar = false;
            lastEntryIsLeftPar = false;
            lastEntryIsEqual = false;
            commaEntered = false;
            indexOperatorScreen = AddElementIntTab(indexOperatorScreen, getTextScreen().length());
        }
        else if (test){
            setTextScreen(operator, true);
        }
    }

    public void MakeOperations(int begin, int end){
        // Substring to treat
        int lengthlistToTreatNb = 1+end-begin;
        int lengthlistToTreatOp = end-begin;
        Double[] listToTreatNb = new Double[lengthlistToTreatNb];
        String[] listToTreatOp = new String[lengthlistToTreatOp];
        for(int i=begin;i<end+1;i++) listToTreatNb[i-begin] = nb[i];
        for(int i=begin;i<end;i++) listToTreatOp[i-begin] = op[i];

        // * and / operations
        int index = 0;
        int compteur = 0;
        boolean timeOrDivInOp = false;
        for(String s:listToTreatOp){
            if("*".equals(s) || "/".equals(s)){timeOrDivInOp = true; index = compteur;}
            compteur += 1;
        }

        while(timeOrDivInOp){
            double nb1 = listToTreatNb[index]; double nb2 = listToTreatNb[index + 1];
            switch(listToTreatOp[index]){
                case "*": listToTreatNb[index] = nb1*nb2; break;
                case "/": listToTreatNb[index] = nb1/nb2; break;
            }
            listToTreatNb = RemoveElementDoubleTab(listToTreatNb,index+1);
            listToTreatOp = RemoveElementStringTab(listToTreatOp,index);
            lengthlistToTreatNb -= 1; lengthlistToTreatOp -= 1;

            compteur = 0;
            timeOrDivInOp = false;
            for(String s:listToTreatOp){
                if("*".equals(s) || "/".equals(s)){timeOrDivInOp = true; index = compteur;}
                compteur += 1;
            }
        }

        // Others operations
        while(lengthlistToTreatOp > 0){
            // Second + and - operations
            double nb1 = listToTreatNb[0]; double nb2 = listToTreatNb[1];
            switch(listToTreatOp[0]){
                case "+": listToTreatNb[0] = nb1+nb2; break;
                case "-": listToTreatNb[0] = nb1-nb2; break;
            }
            listToTreatOp = RemoveElementStringTab(listToTreatOp,0);
            listToTreatNb = RemoveElementDoubleTab(listToTreatNb,1);
            lengthlistToTreatNb -= 1; lengthlistToTreatOp -= 1;
        }

        // Replace substring treated in nb and op by calculated value
        lengthNb -= end-begin; lengthOp -= end-begin;
        nb[begin] = listToTreatNb[0];
        for(int i=begin+1;i<end+1;i++) nb = RemoveElementDoubleTab(nb,begin+1);
        for(int i=begin;i<end;i++) op = RemoveElementStringTab(op,begin);
        for(int i=0;i<indexParLeft.length;i++){if(indexParLeft[i]>begin) indexParLeft[i]-=end-begin;}
        for(int i=0;i<indexParRight.length;i++){if(indexParRight[i]>begin) indexParRight[i]-=end-begin;}
    }

    public void OnClickEqual (View view){
        if(!lastEntryIsOperator && !lastEntryIsLeftPar) {
            // Finish to build nb tab
            lengthNb += 1;
            int nbParRight=0;
            for(int i:indexParRight){if(i==lengthNb-1) nbParRight+=1;}
            if(lastEntryIsRightPar) nb= AddElementDoubleTab(nb,Double.parseDouble(getTextScreen().substring(indexOperatorScreen[indexOperatorScreen.length-nbParRight-1],getTextScreen().length()-nbParRight)));
            else nb = AddElementDoubleTab(nb,Double.parseDouble(getTextScreen().substring(indexOperatorScreen[indexOperatorScreen.length-1])));

            // Close parenthesis still opened
            for(int i=0;i<parOpened;i++) indexParRight = AddElementIntTab(indexParRight,lengthNb-1);

            // Treatment of operations in parenthesis
            while(indexParLeft.length != 0){
                int begin = indexParLeft[indexParLeft.length-1];
                int end = 0;
                boolean endAffected = false;
                int index = -1;
                while(!endAffected) {
                    index += 1;
                    if(indexParRight[index]>=begin){end=indexParRight[index]; endAffected=true;}
                }

                if(begin != end) MakeOperations(begin,end);
                // Remove last element from indexParLeft and indexParRight[index] from indexParRight
                indexParLeft = RemoveElementIntTab(indexParLeft,indexParLeft.length-1);
                indexParRight = RemoveElementIntTab(indexParRight,index);
            }

            // Treatment of the parenthesis-less operations
            MakeOperations(0, lengthNb - 1);

            // Display of the result
            String result = "";
            result = Double.toString(nb[0]);
            // If int then display as such (and not as double with .0)
            int n = result.indexOf(",");
            if(n==-1 && result.substring(result.length()-2).equals(".0")) result = result.substring(0,result.length()-2);
            // Display depending on number in options
            else{
                int pow = (int) Math.pow(10,nbChiffresApresVirgule);
                double r = (double) ((int) (Double.parseDouble(result)*pow))/pow;
                result = Double.toString(r);
            }

            setTextScreen(result,false);
            indexOperatorScreen = new int[]{0};
            nb = new Double[0]; op = new String[0];
            lengthNb = 0; lengthOp = 0;
            lastEntryIsEqual = true;
            lastEntryIsLeftPar = false;
            lastEntryIsRightPar = false;
            commaEntered = false;
        }
    }

    public void OnClickErase (View view){
        String str = getTextScreen();
        if(str.length()!=0) {
            String suppressedString = str.substring(str.length() - 1);
            setTextScreen(str.substring(0, str.length() - 1), false);
            boolean testOperator = false;
            boolean testLeftPar = false;
            boolean testRightPar = false;
            boolean testComma = false;
            switch (suppressedString) {
                case "+": testOperator = true; break;
                case "-": testOperator = true; break;
                case "*": testOperator = true; break;
                case "/": testOperator = true; break;
                case "(": testLeftPar = true; break;
                case ")": testRightPar = true; break;
                case ".": testComma = true; break;
                default:
            }
            if (testOperator) {
                if(Math.floor(nb[nb.length-1])==nb[nb.length-1]) commaEntered = false;
                else commaEntered = true;
                op = RemoveElementStringTab(op, lengthOp - 1);
                nb = RemoveElementDoubleTab(nb, lengthNb - 1);
                lengthOp -= 1;
                lengthNb -= 1;
                lastEntryIsOperator = false;
                if (indexParRight.length != 0 && indexParRight[indexParRight.length - 1] == lengthNb)
                    lastEntryIsRightPar = true;
                indexOperatorScreen = RemoveElementIntTab(indexOperatorScreen, indexOperatorScreen.length - 1);
            }
            if (testLeftPar) {
                lastEntryIsLeftPar = false;
                indexParLeft = RemoveElementIntTab(indexParLeft, indexParLeft.length - 1);
                indexOperatorScreen = RemoveElementIntTab(indexOperatorScreen, indexOperatorScreen.length - 1);
            }
            if (testRightPar) {
                lastEntryIsRightPar = false;
                indexParRight = RemoveElementIntTab(indexParRight, indexParRight.length - 1);
                indexOperatorScreen = RemoveElementIntTab(indexOperatorScreen, indexOperatorScreen.length - 1);
            }
            if(testComma){
                commaEntered = false;
            }
            lastEntryIsEqual = false;
        }
    }

    public void OnClickReset (View view){
        setTextScreen("",false);
        lastEntryIsOperator = false;
        lastEntryIsEqual = false;
        lastEntryIsRightPar = false;
        lastEntryIsLeftPar = false;
        indexOperatorScreen = new int[]{0};
        parOpened = 0;
        commaEntered = false;
        nb = new Double[0]; op = new String[0];
        indexParLeft = new int[0]; indexParRight = new int[0];
        lengthNb = 0; lengthOp = 0;
    }

    public void OnClickOption(View view){
        optionsDialog.setContentView(R.layout.popup);
        // Set options to those selected previously
        final EditText edittext = (EditText) optionsDialog.findViewById(R.id.editTextOptionNbSign);
        final Switch s = (Switch) optionsDialog.findViewById(R.id.switchOptionRadDeg);
        final Spinner sp = (Spinner) optionsDialog.findViewById(R.id.spinnerLanguage);
        edittext.setText(Integer.toString(nbChiffresApresVirgule));
        s.setChecked(anglesEnDegres);
        sp.setSelection(((ArrayAdapter) sp.getAdapter()).getPosition(language));

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(language != sp.getSelectedItem().toString()){
                    language = sp.getSelectedItem().toString();
                    switch(language){
                        case "Français": setAppLocale("fr"); break;
                        case "English": setAppLocale("en"); break;
                        case "Español": setAppLocale("es"); break;
                        case "Deutsch": setAppLocale("de"); break;
                    }
                    optionsDialog.dismiss();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});

        Button closeBtn = optionsDialog.findViewById(R.id.closeBtnPopUp);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Options recovery
            nbChiffresApresVirgule = (int) Integer.parseInt(edittext.getText().toString());
            anglesEnDegres = (boolean) s.isChecked();
            optionsDialog.dismiss();
            }
        });
        optionsDialog.show();
    }

    public void OnClickParLeft(View view){
        if(lastEntryIsEqual){ OnClickReset(view); lastEntryIsEqual = false;}
        if(lastEntryIsOperator || getTextScreen().length()==0){
            setTextScreen(getTextButtonPressed(view),true);
            parOpened += 1;
            // Recover the index of the first number in parenthesis
            indexParLeft = AddElementIntTab(indexParLeft,lengthNb);
            indexOperatorScreen = AddElementIntTab(indexOperatorScreen,getTextScreen().length());
            lastEntryIsLeftPar = true;
        }
    }

    public void OnClickParRight(View view){
        if(parOpened>0 && !lastEntryIsOperator && !lastEntryIsLeftPar){
            setTextScreen(getTextButtonPressed(view),true);
            parOpened -= 1;
            // Recover the index of the last number in parenthesis
            indexParRight = AddElementIntTab(indexParRight,lengthNb);
            indexOperatorScreen = AddElementIntTab(indexOperatorScreen,getTextScreen().length());
            lastEntryIsRightPar = true;
        }
    }
}
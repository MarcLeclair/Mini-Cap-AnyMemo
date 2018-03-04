package org.liberty.android.fantastischmemo.utils;

import android.text.TextUtils;
import android.widget.EditText;

import com.andreabaccega.formedittextvalidator.Validator;

/**
 * Created by User on 2018-03-04.
 */

public class InputValidator  extends Validator{
    private  String compare = "";
    public InputValidator(String customErrorMessage) {
        super(customErrorMessage);
    }


    public void setComparator(String compare){
        this.compare = compare;
    }
    @Override
    public boolean isValid(EditText spellHint) {
        return TextUtils.equals(spellHint.getText(), "ciao");
    }

}

package fri.muc.peterus.muc_hw.helpers;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by peterus on 25.10.2015.
 */
public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new AsteriskCharSequence(source);
    }

    private class AsteriskCharSequence implements CharSequence{
        private CharSequence password;
        public AsteriskCharSequence(CharSequence source){
            password = source;
        }

        @Override
        public int length() {
            return password.length();
        }

        @Override
        public char charAt(int index) {
            return '*';
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return password.subSequence(start, end);
        }
    }
}

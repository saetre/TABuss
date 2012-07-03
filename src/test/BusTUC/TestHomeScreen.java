/***
 * Just an extremely simply test-package to keep running updates of
 * org.ubicompforall.BusTUC (or as.andors.BusTUC) within the original test-package
 * on Google Play
 */

package test.BusTUC;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TestHomeScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this,
				org.ubicompforall.BusTUC.Main.Homescreen.class));
	}// onCreate

}// class HomeScreen

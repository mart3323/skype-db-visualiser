package Loader;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AccountsListController {

    @FXML
    private GridPane grid;
    @FXML
    void initialize() { // This method is called by the FXMLLoader when initialization is complete
        ArrayList<String> account_names = getAccounts();

        int row = 0;
        for (String name : account_names) {
            new Account(name, grid, row);
            row++;
        }

    }

    private ArrayList<String> getAccounts() {
        File file = new File(System.getenv("APPDATA")+"/Skype");
        String[] directories = file.list(new FilenameFilter() {
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        ArrayList<String> account_names = new ArrayList<String>(){};
        account_names.addAll(Arrays.asList("mart3323est","m3est"));
        //Collections.addAll(account_names, directories);

        account_names.remove("Content");
        account_names.remove("DataRv");
        account_names.remove("My Skype Received Files");
        account_names.remove("shared_dynco");
        account_names.remove("shared_httpfe");
        return account_names;
    }
}

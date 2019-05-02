package com.example.rongjian.googlemap.models;

import android.util.Log;
import static com.example.rongjian.googlemap.models.FuzzyMatch.getRatio;
public class DIYplaces {
    private static final String TAG = "DIYClass";

    private String list;
    private double Lat;
    private double Lng;

    private String[] name = {"MSI Analytical Lab", "MSIAL", "msi analytical",
            "Orfalea Center for Global and International Studies","orfalea center", "OCGIS", "Orfalea Center",
            "Public Safety", "public safety",
            "Studio", "studio",
            "Institute for Terahertz Science and Technology ", "ITST", "Terahertz Science",
            "Biological Sciences Administration", "biological sciences",
            "Facilities Management", "facilities management",
            "Harder South", "harder south",
            "Mail Services", "mail services",
            "El Centro", "el centro",
            "Parking Lot 37", "parking 37", "lot37",
            "Parking Lot 22", "parking 22", "lot22",
            "Parking Lot 16", "parking 16", "lot16",
            "Parking Lot 18", "parking 18", "lot18",
            "Parking Lot 27", "parking 27", "lot27",
            "Physics Trailer 2",
            "Physics Trailer 1",
            "Trailer 698",
            "Trailer 384",
            "Trailer 699",
            "Trailer 697",
            "Trailer 380",
            "Trailer 936",
            "Trailer 935",
            "Trailer 232",

    };
    private double[] lat = { 34.412560, 34.412560, 34.412560,
            34.413832, 34.413832, 34.413832, 34.413832,
            34.421968, 34.421968,
            34.412546, 34.412546,
            34.414102, 34.414102, 34.414102,
            34.411750, 34.411750,
            34.420277, 34.420277,
            34.419734, 34.419734,
            34.423130, 34.423130,
            34.414172, 34.414172,
            34.423107, 34.423107, 34.423107,
            34.413505, 34.413505, 34.413505,
            34.417891, 34.417891, 34.417891,
            34.417529, 34.417529, 34.417529,
            34.414722, 34.414722, 34.414722,
            34.414213,
            34.414079,
            34.413732,
            34.413722,
            34.413712,
            34.413716,
            34.413725,
            34.416178,
            34.416070,
            34.415982,

    };
    private double[] lng = { -119.842233, -119.842233, -119.842233,
            -119.847530, -119.847530,  -119.847530, -119.847530,
            -119.853461, -119.853461,
            -119.850868, -119.850868,
            -119.843942, -119.843942, -119.843942,
            -119.843239, -119.843239,
            -119.852416, -119.852416,
            -119.854578, -119.854578,
            -119.856859, -119.856859,
            -119.844450, -119.844450,
            -119.856354, -119.856354, -119.856354,
            -119.852605, -119.852605, -119.852605,
            -119.846858, -119.846858, -119.846858,
            -119.847675, -119.847675, -119.847675,
            -119.851313, -119.851313, -119.851313,
            -119.843936,
            -119.843939,
            -119.842408,
            -119.842530,
            -119.842653,
            -119.842735,
            -119.842853,
            -119.843536,
            -119.843526,
            -119.843526
    };

    public String[] name() {
        return name;
    }

    public int getList(String search){
        Log.d(TAG, "search value: " + search);
        for (int i = 0; i < name.length; i++)
            if (getRatio(search, name[i], false) >= 100){
                list = name[i];
                return i;
        }
        return 0;
    }

    public double getLat(int s){

                return lat[s];


    }

    public double getLng(int h) {

        return lng[h];
    }


}

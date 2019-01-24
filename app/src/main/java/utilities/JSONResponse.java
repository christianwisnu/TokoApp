package utilities;

import model.list.ListKabKota;
import model.list.ListKecamatan;
import model.list.ListProvinsi;

/**
 * Created by Chris on 25/03/2018.
 */

public class JSONResponse {
    private ListProvinsi[] province;
    public ListProvinsi[] getProvince() {
        return province;
    }

    private ListKabKota[] kabkota;
    public ListKabKota[] getKabkota() {
        return kabkota;
    }

    private ListKecamatan[] kecamatan;
    public ListKecamatan[] getKecamatan() {
        return kecamatan;
    }
}

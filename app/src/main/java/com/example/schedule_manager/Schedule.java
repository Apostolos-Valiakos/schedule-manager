    package com.example.schedule_manager;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Random;

    public class Schedule {
        String date;
        String vardia;
        String onoma;
        String epitheto;
        String eidikothta;

        static ArrayList<Schedule> sched = new ArrayList<>();
        static public ArrayList<Schedule> onCreate(int diarkeia, String firDate) {

            List<Ergazomenoi> ergazomenoiList = MainActivity.getErgazomenoiArrayList();
            HashMap<String, String> shiftsMap = MainActivity.getShiftsMap();
            List<Vardies> vardiesList = MainActivity.getVardiesList();
            Matrix matrix[] = new Matrix[ergazomenoiList.size()];
            int i = 0;

            for (Ergazomenoi erg : ergazomenoiList) {
                matrix[i] = new Matrix(erg);
                i++;
            }
            for(int meres =0; meres <diarkeia; meres++) {
                ArrayList<String> arg = (ArrayList<String>) MainActivity.getArgeiesList();
                for(String argia : arg){
                    if(argia.equals(firDate)){
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(firDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, 1);  // number of days to add
                        firDate = sdf.format(c.getTime());
                    }
                }
                //if(firDate.equals())
                createSchedule(matrix, vardiesList, shiftsMap);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(sdf.parse(firDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, 1);  // number of days to add
                firDate = sdf.format(c.getTime());
            }
            /*for(int j=0; j < sched.size(); j++){
                if(j%18 == 0) {
                    Log.wtf("tag", "New Day");
                }
                String pro = sched.get(j).vardia + " "+ sched.get(j).onoma + " "+sched.get(j).epitheto + " "+sched.get(j).eidikothta +"\n";
                Log.wtf("tag",""+pro);
            }*/
            return sched;
        }

        static public List<Schedule> createSchedule(Matrix[] matrix_arr, List<Vardies> vardies, HashMap<String, String> shiftsMap) {
            String on_vardias;
            String on_eidik;
            int ar_erg;
            int min = 0;
            boolean found = false;

                clearPicked(matrix_arr);
                for (Vardies vardia : vardies) {
                    HashMap<Matrix,Integer> ergazomenoiIdiasWras2 = new HashMap<>();
                    on_vardias = vardia.getOnoma();
                    on_eidik = vardia.getEidikotita();
                    ar_erg = vardia.getEmployeesNo();
                    min = minHoursOnVard(matrix_arr, on_vardias);
                        for (Matrix matrix : matrix_arr) {
                            found = false;
                            if (matrix.getErgazomenos().getEidikotita().equals(on_eidik) && ar_erg > 0 && !matrix.isPicked() && matrix.checkSeqDays(matrix) && matrix.getTotalHours() >= 0) {
                                if (hasLeastHours(matrix, min, on_vardias)) {
                                    sched.add(new Schedule(shiftsMap.get(on_vardias), matrix.getErgazomenos().getOnoma(), matrix.getErgazomenos().getEpitheto(), on_eidik));
                                    matrix.addHours(on_vardias);
                                    min = matrix.getHours(on_vardias);
                                    matrix.remHours();
                                    //matrix.seqDays++;
                                    min = matrix.getHours(on_vardias);
                                    ar_erg--;
                                    matrix.pick();
                                    found = true;
                                } else if (hasEqualsHours(matrix, min, on_vardias)) {
                                    ergazomenoiIdiasWras2.put(matrix, matrix.getTotalHours());
                                }
                            }
                        }
                        if (!found || ergazomenoiIdiasWras2.size() > ar_erg) {
                            if(ergazomenoiIdiasWras2.size() < ar_erg){
                                Matrix[] matrixArrayList = new Matrix[100];
                                int i =0;
                                for(Matrix mtr : matrix_arr){
                                    if(mtr.getErgazomenos().getEidikotita().equals(on_eidik) && mtr.isPicked() == false){
                                        matrixArrayList[i] = mtr;
                                        i++;
                                    }
                                }
                                while(ergazomenoiIdiasWras2.size() < ar_erg) {
                                    Random rand = new Random();
                                    int pos = rand.nextInt(matrixArrayList.length);
                                    Matrix mtr = matrixArrayList[pos];
                                    if (mtr != null) {
                                        ergazomenoiIdiasWras2.put(mtr, mtr.getTotalHours());
                                    }
                                }
                            }
                                while (ar_erg != 0) {
                                    Matrix matrix = getLeastTotal(ergazomenoiIdiasWras2);
                                    sched.add(new Schedule(shiftsMap.get(on_vardias), matrix.getErgazomenos().getOnoma(), matrix.getErgazomenos().getEpitheto(), on_eidik));
                                    matrix.addHours(on_vardias);
                                    min = matrix.getHours(on_vardias);
                                    matrix.remHours();
                                    //matrix.seqDays++;
                                    ar_erg--;
                                    matrix.pick();
                                    ergazomenoiIdiasWras2.remove(matrix.getErgazomenos());
                                 }
                        }
                    }

            return sched;
        }

        private static int minHoursOnVard(Matrix[] matrix, String on_vard) {
            int hours=1000;
            for(Matrix mtr : matrix){
                if(mtr.getHours(on_vard) < hours && mtr.getErgazomenos().getEidikotita().equals(on_vard)){
                    hours = mtr.getHours(on_vard);
                }
            }
            return hours;

        }

        public Schedule(String Date, String vardia, String onoma, String epitheto, String eidikothta) {
            this.date = Date;
            this.vardia = vardia;
            this.onoma = onoma;
            this.epitheto = epitheto;
            this.eidikothta = eidikothta;
        }
        public static Matrix getLeastTotal(HashMap<Matrix,Integer> ergazomenoi) {
            int elaxistes = 1000;
            for (int el : ergazomenoi.values()) {
                if (el < elaxistes) {
                    elaxistes = el;
                }
            }
            Matrix matrix_to_return = null;
            for (Matrix matrix : ergazomenoi.keySet()) {
                if (matrix.getTotalHours() <= elaxistes)
                   matrix_to_return = matrix;
            }
            ergazomenoi.remove(matrix_to_return);
            return matrix_to_return;
        }

        static public void clearPicked(Matrix[] matrix){
            for(Matrix mtr: matrix){
                mtr.clrPick();
            }
        }

        public static boolean hasLeastHours(Matrix matrix, int min, String vardia){

            if(vardia.equals("6")){
                if(matrix.getWres6()<min){
                    return true;
                }
            }
            else if(vardia.equals("1")){
                if(matrix.getWres1()<min){
                    return true;
                }
            }
            else if(vardia.equals("2")){
                if(matrix.getWres2()<min){
                    return true;
                }
            }
            else if(vardia.equals("3")){
                if(matrix.getWres3()<min){
                    return true;
                }
            }
            else if(vardia.equals("4")){
                if(matrix.getWres4()<min){
                    return true;
                }
            }
            else if(vardia.equals("5")){
                if(matrix.getWres5()<min){
                    return true;
                }
            }
                return false;
        }
        public static boolean hasEqualsHours(Matrix matrix, int min, String vardia){
            if(vardia.equals("6")){
                if(matrix.getWres6()==min || matrix.getWres6()==min+4 ){
                    return true;
                }
            }
            else if(vardia.equals("1")){
                if(matrix.getWres1()==min || matrix.getWres1()==min+4){
                    return true;
                }
            }
            else if(vardia.equals("2")){
                if(matrix.getWres2()==min || matrix.getWres2()==min+4){
                    return true;
                }
            }
            else if(vardia.equals("3")|| matrix.getWres3()==min+4){
                if(matrix.getWres3()==min){
                    return true;
                }
            }
            else if(vardia.equals("4") || matrix.getWres4()==min+4){
                if(matrix.getWres4()==min){
                    return true;
                }
            }
            else if(vardia.equals("5") || matrix.getWres5()==min+4){
                if(matrix.getWres5()==min){
                    return true;
                }
            }
            return false;
        }
        public static Matrix pickRand(Matrix[] matrix){
            Random rand = new Random();
            int pos = rand.nextInt(matrix.length);
            Matrix mtr = matrix[pos];
            return mtr;
        }
        public static Matrix pickLeastTotHours(Matrix[] matrices){
            int ela=matrices[0].getTotalHours();
            Matrix[] matrixArrayList = new Matrix[matrices.length];
            int j =0;

            for(int i=0;i<matrices.length;i++){
                if(matrices[i].getTotalHours()<=ela){
                    ela = matrices[i].getTotalHours();
                    matrixArrayList[j] = (matrices[i]);
                    j++;
                }
            }
            Matrix returnedMatrix = pickRand(matrixArrayList);
            return returnedMatrix;
        }

        public static boolean checkErg(){
            ArrayList<Vardies> ArAtomonSeVardies = (ArrayList<Vardies>) MainActivity.getVardiesList();
            int empNo,empNoNeeded;
            for(Vardies vrd : ArAtomonSeVardies) {
                empNo =0;
                empNoNeeded = vrd.getEmployeesNo();
                for (Ergazomenoi erg :MainActivity.getErgazomenoiArrayList()) {
                    if (vrd.getEidikotita().equals(erg.eidikotita)){
                        empNo++;
                    }
                }
                if(empNo < empNoNeeded){
                    return false;
                }
            }
            return true;
        }

        public Schedule(String vardia, String onoma, String epitheto, String eidikothta) {
            this.vardia = vardia;
            this.onoma = onoma;
            this.epitheto = epitheto;
            this.eidikothta = eidikothta;
        }

        public String getVardia() {
            return vardia;
        }

        public String getOnoma() {
            return onoma;
        }

        public String getEpitheto() {
            return epitheto;
        }

        public String getEidikothta() {
            return eidikothta;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }

    }
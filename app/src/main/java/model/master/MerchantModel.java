package model.master;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MerchantModel implements Serializable {

    private MerchantHeaderModel header;
    private List<MerchantDetailModel> scheduleList;
    private List<MerchantPictureModel> pictureList;

    public MerchantModel(){
        this.scheduleList = new ArrayList<MerchantDetailModel>();
        this.pictureList = new ArrayList<MerchantPictureModel>();
    }

    public MerchantHeaderModel getHeader() {
        return header;
    }

    public void setHeader(MerchantHeaderModel header) {
        this.header = header;
    }

    public List<MerchantDetailModel> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<MerchantDetailModel> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public List<MerchantPictureModel> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<MerchantPictureModel> pictureList) {
        this.pictureList = pictureList;
    }

    public void addItemPicture(MerchantPictureModel itemModel){
        pictureList.add(itemModel);
    }

    public void removeItemPicture(int line){
        Iterator<MerchantPictureModel> itemIterator = pictureList.iterator();
        while(itemIterator.hasNext()){
            MerchantPictureModel item = itemIterator.next();
            if(item.getLine().intValue() == line){
                itemIterator.remove();
            }
        }
    }

    public void removeAllItemPicture(){
        Iterator<MerchantPictureModel> itemIterator = pictureList.iterator();
        while(itemIterator.hasNext()){
            itemIterator.remove();
        }
    }
}

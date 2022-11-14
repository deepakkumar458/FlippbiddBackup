package com.flippbidd.Adapter.UploadList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flippbidd.Adapter.SubribsionPlan.SubcribsionPlanList;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.Plan.Plandata;
import com.flippbidd.R;
import com.google.gson.JsonElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UploadDocumentAdapterList extends RecyclerView.Adapter<UploadDocumentAdapterList.MyViewHolder> {

            Context moContext;
            List<JsonElement> jsonArrays;
            boolean isDeleteShow;

            onItemClickLisnear mOnItemClickLisnear;

    public UploadDocumentAdapterList(Context mContext, List<JsonElement> moListData,boolean isDeleted) {
                this.moContext = mContext;
                this.isDeleteShow = isDeleted;
                jsonArrays = new ArrayList<>();
                jsonArrays.addAll(moListData);
            }


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(moContext).inflate(R.layout.item_document_ui, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                try {
                    JsonElement mJsonObject = jsonArrays.get(position);

                    holder.bideData(position,mJsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public int getItemCount() {
                return jsonArrays.size();
            }

            public void addData(List<JsonElement> moJsonArrayList){
                jsonArrays.clear();
                jsonArrays.addAll(moJsonArrayList);
                notifyDataSetChanged();
            }

            class MyViewHolder extends RecyclerView.ViewHolder {

                CustomTextView moTvDocumentTitle,moIvDeleteDocument,moTvDocumentDate;
                ImageView moIvDocumentView;


                public MyViewHolder(@NonNull View itemView) {
                    super(itemView);

                    moTvDocumentTitle = itemView.findViewById(R.id.tvDocumentTitle);
                    moIvDeleteDocument = itemView.findViewById(R.id.ivDeleteDocument);
                    moIvDocumentView = itemView.findViewById(R.id.ivDocumentView);
                    moTvDocumentDate = itemView.findViewById(R.id.ivDocumentDate);


                }

                void bideData(int position,JsonElement mJsonObject) {

                    if(isDeleteShow){
                        moIvDeleteDocument.setVisibility(View.VISIBLE);
                    }else {
                        moIvDeleteDocument.setVisibility(View.GONE);
                    }

                    String fileType = mJsonObject.getAsJsonObject().get("file_type").getAsString();

                    if(fileType.equalsIgnoreCase("link")){
                        //Link
                        moIvDocumentView.setImageResource(R.drawable.ic_link);
                    }else if(fileType.equalsIgnoreCase("image")){
                        //Photo
                        moIvDocumentView.setImageResource(R.drawable.ic_photograph);
                    }else {
                        String docUrl =mJsonObject.getAsJsonObject().get("upload_file").getAsString();
                        moIvDocumentView.setImageResource(R.drawable.ic_docs);
                /*//Document
                if(docUrl.toLowerCase().indexOf("pdf") != -1 ){
                    //pdf
                    moIvDocumentView.setImageResource(R.drawable.ic_new_pdf);
                }else {
                    //doc.other

                }*/
                    }

                    moTvDocumentTitle.setText(mJsonObject.getAsJsonObject().get("title").getAsString());

                    String formateData = mJsonObject.getAsJsonObject().get("uploaded_date").getAsString();
                    //2020-09-02 13:43:28

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yy");

                    Date date = null;
                    try {
                        date = inputFormat.parse(formateData);

                        String outputDateStr = outputFormat.format(date);
                        moTvDocumentDate.setText(outputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }




                    //click delete
                    moIvDeleteDocument.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mOnItemClickLisnear!=null){
                                mOnItemClickLisnear.onClickEvent(getAdapterPosition(),mJsonObject,"Delete");
                            }
                        }
                    });

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mOnItemClickLisnear!=null){
                                mOnItemClickLisnear.onClickEvent(getAdapterPosition(),mJsonObject,"View");
                            }
                        }
                    });
                }

    }

    public interface onItemClickLisnear {
        void onClickEvent(int position, JsonElement mJsonObject, String mActionType);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }


}

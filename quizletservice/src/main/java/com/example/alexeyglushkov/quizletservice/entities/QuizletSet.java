package com.example.alexeyglushkov.quizletservice.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by alexeyglushkov on 27.03.16.
 */
public class QuizletSet implements Parcelable {
    private long id;
    private String title;
    private long createDate;
    private long modifiedDate;
    private long publishedDate;
    private boolean hasImages;
    private boolean canEdit;
    private boolean hasAccess;
    private String description;
    private String langTerms;
    private String langDefs;

    private QuizletUser creator;
    private List<QuizletTerm> terms;

    public QuizletSet() {

    }

    public QuizletSet(Parcel parcel) {
        Bundle bundle = parcel.readBundle();
        id = bundle.getLong("id");
        title = bundle.getString("title");
        createDate = bundle.getLong("createDate");
        modifiedDate = bundle.getLong("modifiedDate");
        publishedDate = bundle.getLong("publishedDate");
        hasImages = bundle.getBoolean("hasImages");
        canEdit = bundle.getBoolean("canEdit");
        hasAccess = bundle.getBoolean("hasAccess");
        description = bundle.getString("description");
        langTerms = bundle.getString("langTerms");
        langDefs = bundle.getString("langDefs");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("title",title);
        bundle.putLong("createDate",createDate);
        bundle.putLong("modifiedDate",modifiedDate);
        bundle.putLong("publishedDate",publishedDate);
        bundle.putBoolean("hasImages",hasImages);
        bundle.putBoolean("canEdit",canEdit);
        bundle.putBoolean("hasAccess",hasAccess);
        bundle.putString("description",description);
        bundle.putString("langTerms",langTerms);
        bundle.putString("langDefs",langDefs);

        parcel.writeBundle(bundle);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public long getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(long publishedDate) {
        this.publishedDate = publishedDate;
    }

    public boolean isHasImages() {
        return hasImages;
    }

    public void setHasImages(boolean hasImages) {
        this.hasImages = hasImages;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLangTerms() {
        return langTerms;
    }

    public void setLangTerms(String langTerms) {
        this.langTerms = langTerms;
    }

    public String getLangDefs() {
        return langDefs;
    }

    public void setLangDefs(String langDefs) {
        this.langDefs = langDefs;
    }

    public QuizletUser getCreator() {
        return creator;
    }

    public void setCreator(QuizletUser creator) {
        this.creator = creator;
    }

    public List<QuizletTerm> getTerms() {
        return terms;
    }

    public void setTerms(List<QuizletTerm> terms) {
        this.terms = terms;
    }

    public static final Parcelable.Creator<QuizletSet> CREATOR = new Parcelable.Creator<QuizletSet>() {
        public QuizletSet createFromParcel(Parcel in) {
            return new QuizletSet(in);
        }

        public QuizletSet[] newArray(int size) {
            return new QuizletSet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}

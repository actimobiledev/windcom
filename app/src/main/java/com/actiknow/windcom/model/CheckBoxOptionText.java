package com.actiknow.windcom.model;

public class CheckBoxOptionText {
    int cb_question_id;
    String text;

    public CheckBoxOptionText(int cb_question_id, String text) {
        this.cb_question_id = cb_question_id;
        this.text = text;
    }



    public int getCb_question_id() {
        return cb_question_id;
    }

    public void setCb_question_id(int cb_question_id) {
        this.cb_question_id = cb_question_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

package com.actiknow.windcom.model;

public class Questions {
    int questionnaire_id,question_id,question_type,question_style;
    String question_text,question_options;


    public Questions(int questionnaire_id, int question_id, String question_text, String question_options, int question_type, int question_style) {
        this.questionnaire_id = questionnaire_id;
        this.question_id = question_id;
        this.question_text = question_text;
        this.question_options = question_options;
        this.question_type = question_type;
        this.question_style = question_style;
    }

    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(int questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }

    public String getQuestion_options() {
        return question_options;
    }

    public void setQuestion_options(String question_options) {
        this.question_options = question_options;
    }

    public int getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(int question_type) {
        this.question_type = question_type;
    }

    public int getQuestion_style() {
        return question_style;
    }

    public void setQuestion_style(int question_style) {
        this.question_style = question_style;
    }
}



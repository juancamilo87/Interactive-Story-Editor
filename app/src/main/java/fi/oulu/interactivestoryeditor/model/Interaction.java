package fi.oulu.interactivestoryeditor.model;

import java.io.Serializable;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class Interaction implements Serializable{

    public static final int GPS_INTERACTION = 1;
    public static final int NFC_INTERACTION = 2;
    public static final int QR_INTERACTION = 3;
    public static final int QUIZ_INTERACTION = 4;
    public static final int SPELL_INTERACTION = 5;

    private long interaction_id;
    private int interactionType;
    private String instructions;
    private String positiveTextFeedback;
    private String negativeTextFeedback;
    private String positiveAudioFeedbackUrl;
    private String negativeAudioFeedbackUrl;

    public Interaction() {
        this.interaction_id = -1;
    }

    public Interaction(int interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl) {
        this.interactionType = interactionType;
        this.instructions = instructions;
        this.positiveTextFeedback = positiveTextFeedback;
        this.negativeTextFeedback = negativeTextFeedback;
        this.positiveAudioFeedbackUrl = positiveAudioFeedbackUrl;
        this.negativeAudioFeedbackUrl = negativeAudioFeedbackUrl;
        this.interaction_id = -1;
    }

    public Interaction(int interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl, long interaction_id) {
        this.interactionType = interactionType;
        this.instructions = instructions;
        this.positiveTextFeedback = positiveTextFeedback;
        this.negativeTextFeedback = negativeTextFeedback;
        this.positiveAudioFeedbackUrl = positiveAudioFeedbackUrl;
        this.negativeAudioFeedbackUrl = negativeAudioFeedbackUrl;
        this.interaction_id = interaction_id;
    }

    public int getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(int interactionType) {
        this.interactionType = interactionType;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getPositiveTextFeedback() {
        return positiveTextFeedback;
    }

    public void setPositiveTextFeedback(String positiveTextFeedback) {
        this.positiveTextFeedback = positiveTextFeedback;
    }

    public String getNegativeTextFeedback() {
        return negativeTextFeedback;
    }

    public void setNegativeTextFeedback(String negativeTextFeedback) {
        this.negativeTextFeedback = negativeTextFeedback;
    }

    public String getPositiveAudioFeedbackUrl() {
        return positiveAudioFeedbackUrl;
    }

    public void setPositiveAudioFeedbackUrl(String positiveAudioFeedbackUrl) {
        this.positiveAudioFeedbackUrl = positiveAudioFeedbackUrl;
    }

    public String getNegativeAudioFeedbackUrl() {
        return negativeAudioFeedbackUrl;
    }

    public void setNegativeAudioFeedbackUrl(String negativeAudioFeedbackUrl) {
        this.negativeAudioFeedbackUrl = negativeAudioFeedbackUrl;
    }

    public long getInteraction_id()
    {
        return interaction_id;
    }
}

package fi.oulu.interactivestoryeditor.model;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class Interaction {

    private String interactionType;
    private String instructions;
    private String positiveTextFeedback;
    private String negativeTextFeedback;
    private String positiveAudioFeedbackUrl;
    private String negativeAudioFeedbackUrl;

    public Interaction() {
    }

    public Interaction(String interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl) {
        this.interactionType = interactionType;
        this.instructions = instructions;
        this.positiveTextFeedback = positiveTextFeedback;
        this.negativeTextFeedback = negativeTextFeedback;
        this.positiveAudioFeedbackUrl = positiveAudioFeedbackUrl;
        this.negativeAudioFeedbackUrl = negativeAudioFeedbackUrl;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
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
}

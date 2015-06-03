package fi.oulu.interactivestoryeditor.model;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class QRCodeInteraction extends Interaction {

    private String secretCode;

    public QRCodeInteraction() {
    }

    public QRCodeInteraction(String interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl, String secretCode) {
        super(interactionType, instructions, positiveTextFeedback, negativeTextFeedback, positiveAudioFeedbackUrl, negativeAudioFeedbackUrl);
        this.secretCode = secretCode;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}

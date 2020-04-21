package speedtest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTest {
    private BigDecimal mega;
    private BigDecimal speedInMegaBit;
    private boolean isFinished;
    private SpeedTestSocket speedTestSocket;

    private final String fileURL = "http://bouygues.testdebit.info/1G/1G.iso";

    public SpeedTest() {
        mega = new BigDecimal("1000000");

        createTestSocket();
        download();
    }

    public String getSpeed() {
        return (speedInMegaBit != null) ? speedInMegaBit.toString() : "0";
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    private void createTestSocket() {
        speedTestSocket = new SpeedTestSocket();
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                isFinished = true;
            }

            @Override
            public void onError(SpeedTestError error, String errorMessage) {

            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                speedInMegaBit = report.getTransferRateBit().divide(mega).setScale(4, RoundingMode.HALF_UP);
            }
        });
    }

    private void download() {
        speedTestSocket.startDownload(fileURL);
    }
}
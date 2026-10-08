package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import android.util.Size;

import java.util.List;

/**
 * AprilTag camera test / demo.
 *
 * Setup: in the Robot Controller config, name your webcam "Webcam 1"
 * (or change WEBCAM_NAME below). Set USE_WEBCAM = false to use the phone camera.
 *
 * Controls (gamepad 1):
 *   A = stop camera stream (saves CPU/bandwidth)
 *   B = resume camera stream
 *   DPAD UP / DOWN = raise / lower decimation (higher = faster, shorter range)
 *
 * View the live feed: Driver Station menu -> "Camera Stream"
 * (or the RC phone screen, or `scrcpy`/FTC Dashboard if you use it).
 */
@TeleOp(name = "AprilTag Camera Test", group = "Test")
public class AprilTagCameraTest extends LinearOpMode {

    // ---- Config ----
    private static final boolean USE_WEBCAM = true;
    private static final String WEBCAM_NAME = "Webcam 1";
    private static final int RES_WIDTH = 640;
    private static final int RES_HEIGHT = 480;
    private float decimation = 2;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {
        initAprilTag();

        telemetry.addLine("AprilTag Camera Test");
        telemetry.addLine("Press START to begin. Camera preview: DS menu -> Camera Stream");
        telemetry.update();

        waitForStart();

        boolean lastUp = false, lastDown = false;

        while (opModeIsActive()) {
            // Stream on/off
            if (gamepad1.a) visionPortal.stopStreaming();
            else if (gamepad1.b) visionPortal.resumeStreaming();

            // Decimation adjust (edge-triggered)
            if (gamepad1.dpad_up && !lastUp) decimation = Math.min(decimation + 1, 4);
            if (gamepad1.dpad_down && !lastDown) decimation = Math.max(decimation - 1, 1);
            lastUp = gamepad1.dpad_up;
            lastDown = gamepad1.dpad_down;
            aprilTag.setDecimation(decimation);

            telemetryAprilTag();
            telemetry.update();
            sleep(20);
        }

        visionPortal.close();
    }

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setDrawTagID(true)
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .build();

        aprilTag.setDecimation(decimation);

        VisionPortal.Builder builder = new VisionPortal.Builder();
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, WEBCAM_NAME));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }
        builder.setCameraResolution(new Size(RES_WIDTH, RES_HEIGHT));
        builder.enableLiveView(true);
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);
        builder.setAutoStopLiveView(false);
        builder.addProcessor(aprilTag);

        visionPortal = builder.build();
    }

    private void telemetryAprilTag() {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        telemetry.addData("Camera state", visionPortal.getCameraState());
        telemetry.addData("FPS", "%.1f", visionPortal.getFps());
        telemetry.addData("Decimation", "%.0f  (DPAD up/down)", decimation);
        telemetry.addData("# Tags detected", detections.size());
        telemetry.addLine();

        for (AprilTagDetection d : detections) {
            if (d.metadata != null) {
                telemetry.addLine(String.format("== ID %d (%s) ==", d.id, d.metadata.name));
                telemetry.addLine(String.format("XYZ  %6.1f %6.1f %6.1f  (in)",
                        d.ftcPose.x, d.ftcPose.y, d.ftcPose.z));
                telemetry.addLine(String.format("PRY  %6.1f %6.1f %6.1f  (deg)",
                        d.ftcPose.pitch, d.ftcPose.roll, d.ftcPose.yaw));
                telemetry.addLine(String.format("RBE  %6.1f %6.1f %6.1f  (in, deg, deg)",
                        d.ftcPose.range, d.ftcPose.bearing, d.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("== ID %d (not in tag library) ==", d.id));
                telemetry.addLine(String.format("Center  %6.0f %6.0f  (pixels)",
                        d.center.x, d.center.y));
            }
        }

        telemetry.addLine();
        telemetry.addLine("Key: XYZ = right/forward/up from camera; PRY = pitch/roll/yaw;");
        telemetry.addLine("     RBE = range/bearing/elevation");
    }
}

package org.firstinspires.ftc.teamcode.toplevel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.baselevel.Maxspeed;
import org.firstinspires.ftc.teamcode.baselevel.RobotHardware;
import org.firstinspires.ftc.teamcode.input.MecanumDrive;

/**
 * Drive-only TeleOp extracted from the DECODE Maindrive.
 * Left stick = forward/strafe, right stick X = turn.
 * Shooter, intake, scorer, rotate and autonomous hooks were left behind on purpose.
 */
@TeleOp(name = "Maindrive")
public class Maindrive extends LinearOpMode {

    ElapsedTime runtime = new ElapsedTime();
    RobotHardware robot = new RobotHardware();
    Maxspeed driveMaxSpeed = new Maxspeed();
    MecanumDrive driveLogic = new MecanumDrive();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            driveLogic.calculate(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

            // Normalize so no wheel exceeds full power while keeping the ratios
            driveMaxSpeed.setMax(driveLogic.frontLeftPower, driveLogic.frontRightPower,
                    driveLogic.backLeftPower, driveLogic.backRightPower);
            double max = driveMaxSpeed.getMax();
            if (max > 1.0) {
                driveLogic.frontLeftPower /= max;
                driveLogic.frontRightPower /= max;
                driveLogic.backLeftPower /= max;
                driveLogic.backRightPower /= max;
            }

            robot.frontLeft.setPower(driveLogic.frontLeftPower);
            robot.frontRight.setPower(driveLogic.frontRightPower);
            robot.backLeft.setPower(driveLogic.backLeftPower);
            robot.backRight.setPower(driveLogic.backRightPower);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", driveLogic.frontLeftPower, driveLogic.frontRightPower);
            telemetry.addData("Back left/Right", "%4.2f, %4.2f", driveLogic.backLeftPower, driveLogic.backRightPower);
            telemetry.update();
        }
    }
}

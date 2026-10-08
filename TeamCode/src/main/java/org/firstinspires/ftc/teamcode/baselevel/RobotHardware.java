package org.firstinspires.ftc.teamcode.baselevel;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Drivetrain-only hardware map, extracted from the DECODE robot code.
 * Motor config names and directions are unchanged from DECODE.
 */
public class RobotHardware {
    public DcMotor frontLeft = null;
    public DcMotor backLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backRight = null;

    public void init(HardwareMap myHardwareMap) {
        frontLeft = myHardwareMap.get(DcMotor.class, "front_left");
        backLeft = myHardwareMap.get(DcMotor.class, "back_left");
        frontRight = myHardwareMap.get(DcMotor.class, "front_right");
        backRight = myHardwareMap.get(DcMotor.class, "back_right");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // stop motors for safety
        frontLeft.setPower(0.0);
        backLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backRight.setPower(0.0);
    }
}

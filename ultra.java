import robocode.*;
import robocode.util.Utils;
import java.awt.*;

public class ultra extends AdvancedRobot {
    private boolean moveDirectionForward = true;

    @Override
    public void run() {
       
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        setBodyColor(new Color(20, 20, 20));
        setGunColor(new Color(50, 50, 50));
        setRadarColor(new Color(0, 160, 255));
        setBulletColor(new Color(255, 200, 0));
        setScanColor(new Color(0, 200, 255));
        
        setTurnRadarRight(Double.POSITIVE_INFINITY);

        while (true) {
            double distance = 120; 
            if (moveDirectionForward) {
                setAhead(distance);
            } else {
                setBack(distance);
            }
            setTurnRight(30); 
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
       
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();

        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());
        setTurnGunRightRadians(gunTurn);


        double dist = e.getDistance();
        double firePower = 3.0;
        if (dist > 600) firePower = 1.2;
        else if (dist > 300) firePower = 2.0;

        if (Math.abs(gunTurn) < Math.toRadians(8)) {
            setFire(firePower);
        }

        double radarTurn = Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians());
        double extraTurn = Math.signum(radarTurn) * Math.toRadians(30);
        setTurnRadarRightRadians(radarTurn + extraTurn);

        setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI/2 - getHeadingRadians()));
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
   
        moveDirectionForward = !moveDirectionForward;
        setTurnRight(45);
        if (moveDirectionForward) setAhead(120); else setBack(120);
    }

    @Override
    public void onHitWall(HitWallEvent e) {

        moveDirectionForward = !moveDirectionForward;
        setBack(150);
        setTurnRight(90);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        setTurnRadarRight(Double.POSITIVE_INFINITY);
    }
}

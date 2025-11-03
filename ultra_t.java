import robocode.*;
import robocode.util.Utils;
import java.awt.*;

/**
 * ultraT - um robô simples para o Robocode
 * 
 * Comportamento:
 * - Varre o radar continuamente.
 * - Se move em zigue‑zague para dificultar acertos.
 * - Mira e atira com potência baseada na distância do alvo.
 */
public class ultraT extends AdvancedRobot {
    private boolean moveDirectionForward = true;

    @Override
    public void run() {
        // Ajustes para que o canhão/radar não girem junto com o corpo
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        // Cores
        setBodyColor(new Color(20, 20, 20));
        setGunColor(new Color(50, 50, 50));
        setRadarColor(new Color(0, 160, 255));
        setBulletColor(new Color(255, 200, 0));
        setScanColor(new Color(0, 200, 255));

        // Mantém o radar girando para sempre
        setTurnRadarRight(Double.POSITIVE_INFINITY);

        // Loop principal: pequeno zigue-zague contínuo
        while (true) {
            double distance = 120; // comprimento do passo
            if (moveDirectionForward) {
                setAhead(distance);
            } else {
                setBack(distance);
            }
            setTurnRight(30); // gira um pouco o corpo
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Calcula o ângulo absoluto até o alvo
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();

        // Gira o canhão até o alvo (mira direta)
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());
        setTurnGunRightRadians(gunTurn);

        // Potência de tiro baseada na distância (1.0 a 3.0)
        double dist = e.getDistance();
        double firePower = 3.0;
        if (dist > 600) firePower = 1.2;
        else if (dist > 300) firePower = 2.0;

        // Se o canhão estiver quase alinhado, atira
        if (Math.abs(gunTurn) < Math.toRadians(8)) {
            setFire(firePower);
        }

        // Mantém o radar "trancado" no alvo (pequeno overshoot)
        double radarTurn = Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians());
        double extraTurn = Math.signum(radarTurn) * Math.toRadians(30);
        setTurnRadarRightRadians(radarTurn + extraTurn);

        // Ajusta movimento para lateralizar o alvo
        setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI/2 - getHeadingRadians()));
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // Troca direção ao ser atingido para dificultar mira do inimigo
        moveDirectionForward = !moveDirectionForward;
        setTurnRight(45);
        if (moveDirectionForward) setAhead(120); else setBack(120);
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        // Recuo simples ao bater na parede
        moveDirectionForward = !moveDirectionForward;
        setBack(150);
        setTurnRight(90);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        // Reative varredura ampla quando um alvo morre
        setTurnRadarRight(Double.POSITIVE_INFINITY);
    }
}

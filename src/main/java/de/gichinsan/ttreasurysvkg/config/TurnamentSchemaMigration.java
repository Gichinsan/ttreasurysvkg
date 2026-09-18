package de.gichinsan.ttreasurysvkg.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TurnamentSchemaMigration {

    private static final String AGE_GROUP_CONSTRAINT = "TURNAMENT_AGE_GROUP_CHECK";

    private final JdbcTemplate jdbcTemplate;

    public TurnamentSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void migrateAgeGroupConstraint() {
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList("""
                SELECT c.constraintname, ch.checkdefinition
                FROM sys.sysconstraints c
                JOIN sys.systables t ON c.tableid = t.tableid
                JOIN sys.syschecks ch ON c.constraintid = ch.constraintid
                WHERE t.tablename = 'TURNAMENT'
                  AND c.type = 'C'
                """);

        for (Map<String, Object> constraint : constraints) {
            String definition = String.valueOf(constraint.get("CHECKDEFINITION"));
            if (definition.toUpperCase().contains("AGE_GROUP")) {
                String name = String.valueOf(constraint.get("CONSTRAINTNAME"));
                jdbcTemplate.execute("ALTER TABLE SA.TURNAMENT DROP CONSTRAINT " + quoteIdentifier(name));
            }
        }

        jdbcTemplate.execute("ALTER TABLE SA.TURNAMENT ADD CONSTRAINT " + AGE_GROUP_CONSTRAINT
                + " CHECK (AGE_GROUP IN ('G1', 'G2', 'F', 'F1', 'F2', 'E', 'pending'))");
    }

    private String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }
}
-- Run after the application has created the tables in farmer_goat_register.
-- This replaces existing foreign keys while keeping all existing records.
USE farmer_goat_register;

-- A farmer deletion should automatically remove its goats.
SET @old_farmer_fk = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'goat'
      AND COLUMN_NAME = 'farmer_id'
      AND REFERENCED_TABLE_NAME = 'farmer'
    LIMIT 1
);
SET @drop_farmer_fk = IF(
    @old_farmer_fk IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE `goat` DROP FOREIGN KEY `', @old_farmer_fk, '`')
);
PREPARE cascade_statement FROM @drop_farmer_fk;
EXECUTE cascade_statement;
DEALLOCATE PREPARE cascade_statement;

ALTER TABLE `goat`
    ADD CONSTRAINT `fk_goat_farmer_cascade`
    FOREIGN KEY (`farmer_id`) REFERENCES `farmer` (`id`)
    ON DELETE CASCADE;

-- A goat deletion should automatically remove its vaccinations.
SET @old_goat_fk = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'vaccination'
      AND COLUMN_NAME = 'goat_id'
      AND REFERENCED_TABLE_NAME = 'goat'
    LIMIT 1
);
SET @drop_goat_fk = IF(
    @old_goat_fk IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE `vaccination` DROP FOREIGN KEY `', @old_goat_fk, '`')
);
PREPARE cascade_statement FROM @drop_goat_fk;
EXECUTE cascade_statement;
DEALLOCATE PREPARE cascade_statement;

ALTER TABLE `vaccination`
    ADD CONSTRAINT `fk_vaccination_goat_cascade`
    FOREIGN KEY (`goat_id`) REFERENCES `goat` (`id`)
    ON DELETE CASCADE;

/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package de.montigem.be.database;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.reflections.Reflections;

import javax.persistence.Entity;
import java.util.EnumSet;

public class DatabaseSchemaRetrieval {

  public static void main(String[] args) {

    Configuration configuration = new Configuration();
    configuration.setProperty("connection.driver_class", "com.mysql.jdbc.Driver");
    configuration.setProperty("dialect", "org.hibernate.dialect.MySQL5Dialect");
    configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/testdb");
    configuration.setProperty("hibernate.connection.username", "admin");
    configuration.setProperty("hibernate.connection.password", "pass");
    configuration.setProperty("hibernate.hbm2ddl.auto", "update");
    configuration.setProperty("hibernate.flushMode", "FLUSH_AUTO");
    configuration.setProperty("hibernate.archive.autodetection", "class, hbm");
    configuration.setProperty("hibernate.show_sql", "false");
    configuration.setProperty("use_sql_comments", "false");
    configuration.setProperty("org.hibernate.envers.audit_table_suffix", "_REV");
    configuration.setProperty("org.hibernate.envers.audit_strategy",
        "org.hibernate.envers.strategy.ValidityAuditStrategy");
    configuration.setProperty("org.hibernate.envers.track_entities_changed_in_revision", "true");
    configuration.setProperty("org.hibernate.envers.global_with_modified_flag", "true");
    configuration
        .setProperty("org.hibernate.envers.audit_strategy_validity_store_revend_timestamp", "true");
    configuration.setProperty("hibernate.connection.release_mode", "after_transaction");

    StandardServiceRegistry standardServiceRegistry = new StandardServiceRegistryBuilder()
        .applySettings(configuration.getProperties())
        .build();

    MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);

    new Reflections("de.montigem.be").getTypesAnnotatedWith(Entity.class)
        .forEach(metadataSources::addAnnotatedClass);

    EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.SCRIPT);

    SchemaExport schemaExport = new SchemaExport();
    schemaExport.setHaltOnError(false);
    schemaExport.setFormat(true);
    schemaExport.setDelimiter(";");
    schemaExport.setOutputFile("db-schema.sql");
    Metadata metadata = metadataSources.buildMetadata();
    schemaExport.createOnly(targetTypes, metadata);
    metadata.buildSessionFactory().close();
  }

}

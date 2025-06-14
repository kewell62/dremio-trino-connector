package com.dremio.exec.store.jdbc.conf;

import static com.google.common.base.Preconditions.checkNotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.dremio.exec.store.jdbc.*;
import com.dremio.options.OptionManager;
import com.dremio.services.credentials.CredentialsService;
import org.apache.log4j.Logger;
import com.dremio.exec.catalog.conf.DisplayMetadata;
import com.dremio.exec.catalog.conf.NotMetadataImpacting;
import com.dremio.exec.catalog.conf.Secret;
import com.dremio.exec.catalog.conf.SourceType;
import com.dremio.exec.store.jdbc.JdbcPluginConfig;
import com.dremio.exec.store.jdbc.dialect.arp.ArpDialect;
import com.dremio.exec.store.jdbc.dialect.arp.ArpYaml;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.VisibleForTesting;
import io.protostuff.Tag;

@SourceType(value = "TrinoARP", label = "Trino",uiConfig = "trino-layout.json", externalQuerySupported = true) 
public class TrinoConf extends AbstractArpConf<TrinoConf> {
  private static final String ARP_FILENAME = "arp/implementation/trino-arp.yaml";
  private static final ArpDialect ARP_DIALECT =
      AbstractArpConf.loadArpFile(ARP_FILENAME, (ArpDialect::new));
  private static final String DRIVER = "io.trino.jdbc.TrinoDriver";

  @NotBlank
  @Tag(1)
  @DisplayMetadata(label = "JDBC Trino URL")
  public String connString;

  @Tag(2)
  @DisplayMetadata(label = "Catalog")
  @NotMetadataImpacting
  public String connCatalog;

  @Tag(3)
  @DisplayMetadata(label = " Session Properties (optional)")
  @NotMetadataImpacting
  public String connProperties = "SSL=true&timezone=America/New_York";

  @NotBlank  
  @Tag(4)
  @DisplayMetadata(label = "Username")
  public String username;

  @Secret
  @Tag(5)
  @DisplayMetadata(label = "Password (optional)")
  @NotMetadataImpacting
  public String password;    

  @Tag(6)
  @DisplayMetadata(label = "Record fetch size")
  @NotMetadataImpacting
  public int fetchSize = 200;
  
  @Tag(7)
  @DisplayMetadata(label = "Maximum idle connections")
  @NotMetadataImpacting
  public int maxIdleConns = 8;

  @Tag(8)
  @DisplayMetadata(label = "Connection idle time (s)")
  @NotMetadataImpacting
  public int idleTimeSec = 60;

  @Tag(9)
  @NotMetadataImpacting
  @DisplayMetadata(label = "Grant External Query access")
  public boolean enableExternalQuery = true;  

  @VisibleForTesting
  public String toJdbcConnectionString() {
    final String connString = checkNotNull(this.connString, "Missing Trino URL.");
    final String catalog = checkNotNull(this.connCatalog, "Missing Trino Catalog.");
    final String user = this.username == null ? "" : "user=" + this.username + "&";
    final String pwd  = this.password == null ? "" : "password=" + this.password + "&";
    return String.format("%s/%s?%s%s%s", connString, catalog, user, pwd, this.connProperties);    
  }

  @Override
  @VisibleForTesting
  public JdbcPluginConfig buildPluginConfig(JdbcPluginConfig.Builder configBuilder, CredentialsService credentialsService, OptionManager optionManager) {
         return configBuilder.withDialect(getDialect())
        .withFetchSize(fetchSize)
        .withDatasourceFactory(this::newDataSource)
        .withAllowExternalQuery(enableExternalQuery)
	.withDatabase(this.connCatalog)
	.withShowOnlyConnDatabase(true)
        .addHiddenTableType("FOREIGN TABLE")
        .addHiddenTableType("SYSTEM VIEW")
        .build();

  }

  private CloseableDataSource newDataSource() {
      return DataSources.newGenericConnectionPoolDataSource(DRIVER,
      toJdbcConnectionString(), null, null, null, DataSources.CommitMode.DRIVER_SPECIFIED_COMMIT_MODE, maxIdleConns, idleTimeSec);
  }

  @Override
  public ArpDialect getDialect() {
    return ARP_DIALECT;
  }

  @VisibleForTesting
  public static ArpDialect getDialectSingleton() {
    return ARP_DIALECT;
  }
}
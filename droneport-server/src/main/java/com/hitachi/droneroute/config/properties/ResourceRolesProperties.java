package com.hitachi.droneroute.config.properties;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** YMLファイルから自事業者チェック対象を読み込むプロパティクラス. */
@ConfigurationProperties(prefix = "resourceroles")
@Component
@Getter
@Setter
@NoArgsConstructor
public class ResourceRolesProperties {
  private List<ResourceRoleRule> checkTargets;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ResourceRoleRule {
    private String path;
    private String method;
    private Boolean requireSystemOperator;
  }
}

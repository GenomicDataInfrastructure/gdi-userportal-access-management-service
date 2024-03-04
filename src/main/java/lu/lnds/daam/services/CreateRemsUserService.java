// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package lu.lnds.daam.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lu.lnds.daam.remote.rems.api.RemsUsersApi;
import lu.lnds.daam.remote.rems.model.CreateUserCommand;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class CreateRemsUserService {

  private final String remsApiKey;
  private final String remsBotUser;
  private final RemsUsersApi usersApi;

  @Inject
  public CreateRemsUserService(
      @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
      @ConfigProperty(name = "quarkus.rest-client.rems_yaml.bot-user") String remsBotUser,
      @RestClient RemsUsersApi usersApi) {
    this.remsApiKey = remsApiKey;
    this.remsBotUser = remsBotUser;
    this.usersApi = usersApi;
  }

  public void createRemsUser(String userId, String name, String email) {
    var createUser = CreateUserCommand.builder().userid(userId).name(name).email(email).build();
    usersApi.apiUsersCreatePost(createUser, remsApiKey, remsBotUser);
  }
}

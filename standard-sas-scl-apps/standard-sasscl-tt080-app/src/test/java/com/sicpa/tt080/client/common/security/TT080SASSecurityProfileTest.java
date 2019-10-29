package com.sicpa.tt080.client.common.security;

import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sicpa.standard.client.common.security.SecurityModel;
import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.tt080.TT080TestSpringContextConfig;

import static com.sicpa.tt080.TT080TestUser.FULL_ADMIN;
import static com.sicpa.tt080.TT080TestUser.OPERATOR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("SAS")
@ContextConfiguration(classes = TT080TestSpringContextConfig.class)
public class TT080SASSecurityProfileTest{

    @Autowired private TT080SecurityModelWrapper<SecurityModel> securityModelWrapper;

    @Test
    public void WhenGetUsersIsRequestedFromSecurityModel_ShouldList_FullAdminAndOperatorUsersOnly(){
        //Given
        final List<User> users = securityModelWrapper.getUsers();

        //When
        final List<User> filteredUsers = users.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(OPERATOR.getLogin()) || u.getLogin().equals(FULL_ADMIN.getLogin()))
                .collect(Collectors.toList());

        //Then
        assertThat(filteredUsers, is(users));

    }

    @Test
    public void Admin_ShouldBeAbleTo_DoEverything(){
        //Given
        final User adminUser = securityModelWrapper.getUserByLogin(FULL_ADMIN.getLogin());

        //When
        final boolean hasAllPermissions = securityModelWrapper.hasAllPermissions(adminUser);

        //Then
        assertThat(hasAllPermissions, is(true));
    }

    @Test
    public void Admin_ShouldNotHave_EmptyNorNullPassword(){
        //Given
        final User adminUser = securityModelWrapper.getUserByLogin(FULL_ADMIN.getLogin());

        //When
        final String password = adminUser.getPassword();

        //Then
        assertThat(password, CoreMatchers.allOf(notNullValue(), is(not(""))));
    }

    @Test
    public void Operator_ShouldNotHave_AllPermissions(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean permission = securityModelWrapper.hasAllPermissions(operator);

        //Then
        assertThat(permission, is(false));
    }

    @Test
    public void Operator_ShouldBeAbleTo_RunProductionInStandardMode(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean permission = operator.hasPermission(SasSclPermission.PRODUCTION_MODE_STANDARD);

        //Then
        assertThat(permission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_RunProductionInExportMode(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean permission = operator.hasPermission(SasSclPermission.PRODUCTION_MODE_EXPORT);

        //Then
        assertThat(permission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_RunProductionInRefeedNormalMode(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean permission = operator.hasPermission(SasSclPermission.PRODUCTION_MODE_REFEED_NORMAL);

        //Then
        assertThat(permission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_RunProductionInMaintenanceMode(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean permission = operator.hasPermission(SasSclPermission.PRODUCTION_MODE_MAINTENANCE);

        //Then
        assertThat(permission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_StartApplication(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean startPermission = operator.hasPermission(SasSclPermission.PRODUCTION_START);

        //Then
        assertThat(startPermission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_StopApplication(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean stopPermission = operator.hasPermission(SasSclPermission.PRODUCTION_STOP);

        //Then
        assertThat(stopPermission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_ViewStatistics(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean statisticPermission = operator.hasPermission(SasSclPermission.PRODUCTION_VIEW_STATISTICS);

        //Then
        assertThat(statisticPermission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_ViewReports(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean reportPermission = operator.hasPermission(SasSclPermission.PRODUCTION_REPORT);

        //Then
        assertThat(reportPermission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_ChangeProductionParameters(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean changeParametersPermission = operator.hasPermission(SasSclPermission.PRODUCTION_CHANGE_PARAMETERS);

        //Then
        assertThat(changeParametersPermission, is(true));
    }

    @Test
    public void Operator_ShouldBeAbleTo_ExitTheApplication(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean exitPermission = operator.hasPermission(SasSclPermission.EXIT);

        //Then
        assertThat(exitPermission, is(true));
    }

    @Test
    public void Operator_ShouldNotBeAbleTo_RunProductionInAllMode(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean permission = operator.hasPermission(SasSclPermission.PRODUCTION_MODE_ALL);

        //Then
        assertThat(permission, is(false));
    }

    @Test
    public void Operator_ShouldNotBeAbleTo_RunProductionInCountingMode(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean permission = operator.hasPermission(SasSclPermission.PRODUCTION_MODE_COUNTING);

        //Then
        assertThat(permission, is(false));
    }

    @Test
    public void Operator_ShouldNotBeAbleTo_RunProductionInRefeedCorrectiongMode(){
        //Given
        final User operator = securityModelWrapper.getUserByLogin(OPERATOR.getLogin());

        //When
        final boolean permission = operator.hasPermission(SasSclPermission.PRODUCTION_MODE_REFEED_CORRECTION);

        //Then
        assertThat(permission, is(false));
    }


    @Test
    public void WhenDefaultUserIsRequestedFromSecurityModel_ShouldReturn_OperatorUser(){
        //given
        final User defaultUser = securityModelWrapper.getDefaultUser();

        //When
        final String login = defaultUser.getLogin();

        //Then
        assertThat(login, is(OPERATOR.getLogin()));
    }
}
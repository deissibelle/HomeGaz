package com.homegaz.app.presentation.auth;

import com.homegaz.app.domain.repository.AuthRepository;
import com.homegaz.app.domain.usecase.auth.GetPasswordStrengthUseCase;
import com.homegaz.app.domain.usecase.auth.LoginWithEmailUseCase;
import com.homegaz.app.domain.usecase.auth.LoginWithPhoneUseCase;
import com.homegaz.app.domain.usecase.auth.RegisterWithEmailUseCase;
import com.homegaz.app.domain.usecase.auth.RegisterWithPhoneUseCase;
import com.homegaz.app.domain.usecase.auth.ResetPasswordUseCase;
import com.homegaz.app.domain.usecase.auth.VerifyEmailOtpUseCase;
import com.homegaz.app.domain.usecase.auth.VerifyPhoneOtpUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<RegisterWithEmailUseCase> registerWithEmailUseCaseProvider;

  private final Provider<VerifyEmailOtpUseCase> verifyEmailOtpUseCaseProvider;

  private final Provider<RegisterWithPhoneUseCase> registerWithPhoneUseCaseProvider;

  private final Provider<VerifyPhoneOtpUseCase> verifyPhoneOtpUseCaseProvider;

  private final Provider<LoginWithEmailUseCase> loginWithEmailUseCaseProvider;

  private final Provider<LoginWithPhoneUseCase> loginWithPhoneUseCaseProvider;

  private final Provider<ResetPasswordUseCase> resetPasswordUseCaseProvider;

  private final Provider<GetPasswordStrengthUseCase> getPasswordStrengthUseCaseProvider;

  private final Provider<AuthRepository> repositoryProvider;

  public AuthViewModel_Factory(Provider<RegisterWithEmailUseCase> registerWithEmailUseCaseProvider,
      Provider<VerifyEmailOtpUseCase> verifyEmailOtpUseCaseProvider,
      Provider<RegisterWithPhoneUseCase> registerWithPhoneUseCaseProvider,
      Provider<VerifyPhoneOtpUseCase> verifyPhoneOtpUseCaseProvider,
      Provider<LoginWithEmailUseCase> loginWithEmailUseCaseProvider,
      Provider<LoginWithPhoneUseCase> loginWithPhoneUseCaseProvider,
      Provider<ResetPasswordUseCase> resetPasswordUseCaseProvider,
      Provider<GetPasswordStrengthUseCase> getPasswordStrengthUseCaseProvider,
      Provider<AuthRepository> repositoryProvider) {
    this.registerWithEmailUseCaseProvider = registerWithEmailUseCaseProvider;
    this.verifyEmailOtpUseCaseProvider = verifyEmailOtpUseCaseProvider;
    this.registerWithPhoneUseCaseProvider = registerWithPhoneUseCaseProvider;
    this.verifyPhoneOtpUseCaseProvider = verifyPhoneOtpUseCaseProvider;
    this.loginWithEmailUseCaseProvider = loginWithEmailUseCaseProvider;
    this.loginWithPhoneUseCaseProvider = loginWithPhoneUseCaseProvider;
    this.resetPasswordUseCaseProvider = resetPasswordUseCaseProvider;
    this.getPasswordStrengthUseCaseProvider = getPasswordStrengthUseCaseProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(registerWithEmailUseCaseProvider.get(), verifyEmailOtpUseCaseProvider.get(), registerWithPhoneUseCaseProvider.get(), verifyPhoneOtpUseCaseProvider.get(), loginWithEmailUseCaseProvider.get(), loginWithPhoneUseCaseProvider.get(), resetPasswordUseCaseProvider.get(), getPasswordStrengthUseCaseProvider.get(), repositoryProvider.get());
  }

  public static AuthViewModel_Factory create(
      Provider<RegisterWithEmailUseCase> registerWithEmailUseCaseProvider,
      Provider<VerifyEmailOtpUseCase> verifyEmailOtpUseCaseProvider,
      Provider<RegisterWithPhoneUseCase> registerWithPhoneUseCaseProvider,
      Provider<VerifyPhoneOtpUseCase> verifyPhoneOtpUseCaseProvider,
      Provider<LoginWithEmailUseCase> loginWithEmailUseCaseProvider,
      Provider<LoginWithPhoneUseCase> loginWithPhoneUseCaseProvider,
      Provider<ResetPasswordUseCase> resetPasswordUseCaseProvider,
      Provider<GetPasswordStrengthUseCase> getPasswordStrengthUseCaseProvider,
      Provider<AuthRepository> repositoryProvider) {
    return new AuthViewModel_Factory(registerWithEmailUseCaseProvider, verifyEmailOtpUseCaseProvider, registerWithPhoneUseCaseProvider, verifyPhoneOtpUseCaseProvider, loginWithEmailUseCaseProvider, loginWithPhoneUseCaseProvider, resetPasswordUseCaseProvider, getPasswordStrengthUseCaseProvider, repositoryProvider);
  }

  public static AuthViewModel newInstance(RegisterWithEmailUseCase registerWithEmailUseCase,
      VerifyEmailOtpUseCase verifyEmailOtpUseCase,
      RegisterWithPhoneUseCase registerWithPhoneUseCase,
      VerifyPhoneOtpUseCase verifyPhoneOtpUseCase, LoginWithEmailUseCase loginWithEmailUseCase,
      LoginWithPhoneUseCase loginWithPhoneUseCase, ResetPasswordUseCase resetPasswordUseCase,
      GetPasswordStrengthUseCase getPasswordStrengthUseCase, AuthRepository repository) {
    return new AuthViewModel(registerWithEmailUseCase, verifyEmailOtpUseCase, registerWithPhoneUseCase, verifyPhoneOtpUseCase, loginWithEmailUseCase, loginWithPhoneUseCase, resetPasswordUseCase, getPasswordStrengthUseCase, repository);
  }
}

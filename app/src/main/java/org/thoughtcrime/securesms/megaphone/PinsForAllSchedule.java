package org.thoughtcrime.securesms.megaphone;

import androidx.annotation.VisibleForTesting;

import org.thoughtcrime.securesms.dependencies.ApplicationDependencies;
import org.thoughtcrime.securesms.keyvalue.SignalStore;
import org.thoughtcrime.securesms.logging.Log;
import org.thoughtcrime.securesms.util.FeatureFlags;
import org.thoughtcrime.securesms.util.TextSecurePreferences;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

class PinsForAllSchedule implements MegaphoneSchedule {

  private static final String TAG = Log.tag(PinsForAllSchedule.class);

  @VisibleForTesting
  static final long DAYS_UNTIL_FULLSCREEN = 8888L;

  private final MegaphoneSchedule schedule = new RecurringSchedule(TimeUnit.HOURS.toMillis(2));

  static boolean shouldDisplayFullScreen(long firstVisible, long currentTime) {
    return false;
  }

  @Override
  public boolean shouldDisplay(int seenCount, long lastSeen, long firstVisible, long currentTime) {
    return false;
  }

  private static boolean isEnabled() {
    if (SignalStore.kbsValues().hasPin()) {
      return false;
    }

    if (FeatureFlags.pinsForAllMegaphoneKillSwitch()) {
      return false;
    }

    if (pinCreationFailedDuringRegistration()) {
      return true;
    }

    if (newlyRegisteredRegistrationLockV1User()) {
      return true;
    }

    if (SignalStore.registrationValues().pinWasRequiredAtRegistration()) {
      return false;
    }

    return true;
  }

  private static boolean pinCreationFailedDuringRegistration() {
    return SignalStore.registrationValues().pinWasRequiredAtRegistration() &&
           !SignalStore.kbsValues().hasPin()                               &&
           !TextSecurePreferences.isV1RegistrationLockEnabled(ApplicationDependencies.getApplication());
  }

  private static boolean newlyRegisteredRegistrationLockV1User() {
    return SignalStore.registrationValues().pinWasRequiredAtRegistration() && TextSecurePreferences.isV1RegistrationLockEnabled(ApplicationDependencies.getApplication());
  }
}

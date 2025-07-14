package com.decryptor.encryptor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class AboutInfoPreference extends Preference {

  public AboutInfoPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    setLayoutResource(R.layout.activity_info); // তুমি যে layout শেয়ার করেছো
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);

    View facebook = holder.findViewById(R.id.facebook);
    View telegram = holder.findViewById(R.id.telegram);
    View cardUpdate = holder.findViewById(R.id.update_card);
    View privacypolicyCard = holder.findViewById(R.id.privacypolicy_card);
    View card = holder.findViewById(R.id.about_card);
    if (cardUpdate != null) {
      cardUpdate.setOnClickListener(
          v -> {
            OTAUpdateHelper.hookPreference(getContext());
          });
    }

    if (privacypolicyCard != null) {
      privacypolicyCard.setOnClickListener(
          v -> {
            getContext()
                .startActivity(
                    IntentUtils.openUrl(
                        getContext(),
                        "https://docs.google.com/document/d/1TB6npLHvH_ZuTgE9Bk9G1haBZJ6jcpF-0M8nW6Y6j5c/edit?usp=drivesdk"));
          });
    }

    if (card != null) {
      card.setOnClickListener(
          v -> {
            getContext()
                .startActivity(
                    IntentUtils.openUrl(
                        getContext(), "https://www.facebook.com/share/18wbmDDERe/"));
          });
    }

    if (facebook != null) {
      facebook.setOnClickListener(
          v -> {
            // facebook link খোলার intent
            getContext()
                .startActivity(
                    IntentUtils.openUrl(
                        getContext(), "https://www.facebook.com/share/18wbmDDERe/"));
          });
    }

    if (telegram != null) {
      telegram.setOnClickListener(
          v -> {
            // Telegram link খোলার intent
            getContext()
                .startActivity(IntentUtils.openUrl(getContext(), "https://t.me/md_shamim12"));
          });
    }
  }
}

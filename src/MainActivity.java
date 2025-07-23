package com.example.fakevpn;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView tvFakeIp, tvCountry, tvLogs;
    private Switch switchWireGuard, switchTor, switchDNSCrypt, switchAutoIpChange;
    private Switch switchKillSwitch, switchSplitTunneling, switchMultiHop;
    private Spinner spinnerDnsResolvers;
    private Button btnChangeIp, btnConnect;
    private ProgressBar progressConnecting;
    private ScrollView scrollViewLogs;

    private final Handler handler = new Handler();
    private Timer autoIpChangeTimer;
    private final Random random = new Random();

    private final String[] countries = {
            "Switzerland 🇨🇭",
            "Netherlands 🇳🇱",
            "United States 🇺🇸",
            "Germany 🇩🇪",
            "Japan 🇯🇵",
            "Canada 🇨🇦",
            "France 🇫🇷",
            "United Kingdom 🇬🇧"
    };

    private final String[] fakeLogs = {
            "Connected to relay.wg001.net (Amsterdam)",
            "Tor circuit created: [123.45.67.89] → [10.0.0.5]",
            "DNSCrypt resolver: cloudflare-dns.com OK",
            "WireGuard handshake completed",
            "Tor relay node switched",
            "DNSCrypt resolver: google-dns.com OK",
            "Connection encrypted with WireGuard",
            "Tor circuit refreshed",
            "DNSCrypt resolver: quad9.net OK"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvFakeIp = findViewById(R.id.tvFakeIp);
        tvCountry = findViewById(R.id.tvCountry);
        tvLogs = findViewById(R.id.tvLogs);
        scrollViewLogs = findViewById(R.id.scrollViewLogs);

        switchWireGuard = findViewById(R.id.switchWireGuard);
        switchTor = findViewById(R.id.switchTor);
        switchDNSCrypt = findViewById(R.id.switchDNSCrypt);
        switchAutoIpChange = findViewById(R.id.switchAutoIpChange);

        switchKillSwitch = findViewById(R.id.switchKillSwitch);
        switchSplitTunneling = findViewById(R.id.switchSplitTunneling);
        switchMultiHop = findViewById(R.id.switchMultiHop);

        spinnerDnsResolvers = findViewById(R.id.spinnerDnsResolvers);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dns_resolvers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDnsResolvers.setAdapter(adapter);

        btnChangeIp = findViewById(R.id.btnChangeIp);
        btnConnect = findViewById(R.id.btnConnect);
        progressConnecting = findViewById(R.id.progressConnecting);

        btnChangeIp.setOnClickListener(v -> changeFakeIp());

        btnConnect.setOnClickListener(v -> simulateConnect());

        switchAutoIpChange.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startAutoIpChange();
            } else {
                stopAutoIpChange();
            }
        });

        // Initialize with a fake IP and country
        changeFakeIp();
    }

    private void changeFakeIp() {
        String newIp = generateFakeIp();
        tvFakeIp.setText("IP: " + newIp);
        String country = countries[random.nextInt(countries.length)];
        tvCountry.setText(country);
        addLog("IP changed to " + newIp + " (" + country + ")");
    }

    private String generateFakeIp() {
        return random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256);
    }

    private void addLog(String log) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String newLog = timestamp + " - " + log + "\n";
        tvLogs.append(newLog);

        // Scroll to bottom
        handler.post(() -> scrollViewLogs.fullScroll(View.FOCUS_DOWN));
    }

    private void startAutoIpChange() {
        autoIpChangeTimer = new Timer();
        autoIpChangeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> changeFakeIp());
            }
        }, 0, 10000); // Change IP every 10 seconds
        addLog("Auto IP Change enabled");
    }

    private void stopAutoIpChange() {
        if (autoIpChangeTimer != null) {
            autoIpChangeTimer.cancel();
            autoIpChangeTimer = null;
        }
        addLog("Auto IP Change disabled");
    }

    private void simulateConnect() {
        btnConnect.setEnabled(false);
        progressConnecting.setVisibility(View.VISIBLE);
        addLog("Connecting...");

        handler.postDelayed(() -> {
            progressConnecting.setVisibility(View.GONE);
            btnConnect.setEnabled(true);
            addLog("Connected");
        }, 5000); // Simulate 5 seconds connection time
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoIpChange();
    }
}

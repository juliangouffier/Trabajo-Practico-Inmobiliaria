package com.example.trabajopracticoinmobiliaria;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;
import com.example.trabajopracticoinmobiliaria.databinding.ActivityMainBinding;
import com.example.trabajopracticoinmobiliaria.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration configuracionBarra;
    private ActivityMainBinding enlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!ApiClient.haySesion(this)) {
            irAInicioSesionYFinalizar();
            return;
        }

        enlace = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(enlace.getRoot());

        setSupportActionBar(enlace.appBarMain.toolbar);
        NavHostFragment fragmentoAnfitrion = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        if (fragmentoAnfitrion == null) {
            throw new IllegalStateException("NavHostFragment no encontrado");
        }
        NavController controladorNavegacion = fragmentoAnfitrion.getNavController();

        configuracionBarra = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_perfil,
                R.id.nav_inmuebles,
                R.id.nav_inquilinos,
                R.id.nav_contratos)
                .setOpenableLayout(enlace.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, controladorNavegacion, configuracionBarra);

        NavigationView vistaNavegacion = enlace.navView;
        if (vistaNavegacion != null) {
            vistaNavegacion.setNavigationItemSelectedListener(item -> {
                boolean manejado = NavigationUI.onNavDestinationSelected(item, controladorNavegacion);
                if (manejado) {
                    enlace.drawerLayout.closeDrawers();
                }
                return manejado;
            });
            sincronizarSeleccionCajon(vistaNavegacion, controladorNavegacion.getCurrentDestination() != null
                    ? controladorNavegacion.getCurrentDestination().getId()
                    : R.id.nav_home);
            controladorNavegacion.addOnDestinationChangedListener((controller, destination, arguments) ->
                    sincronizarSeleccionCajon(vistaNavegacion, destination.getId()));
        }
    }

    private void sincronizarSeleccionCajon(@NonNull NavigationView vistaNavegacion, int destinoId) {
        android.view.Menu menu = vistaNavegacion.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem elemento = menu.getItem(i);
            if (elemento.hasSubMenu()) {
                android.view.SubMenu subMenu = elemento.getSubMenu();
                if (subMenu != null) {
                    for (int j = 0; j < subMenu.size(); j++) {
                        MenuItem subItem = subMenu.getItem(j);
                        subItem.setChecked(subItem.getItemId() == destinoId);
                    }
                }
            } else if (elemento.isCheckable()) {
                elemento.setChecked(elemento.getItemId() == destinoId);
            }
        }
    }

    private void irAInicioSesionYFinalizar() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean resultado = super.onCreateOptionsMenu(menu);
        NavigationView vistaNavegacion = findViewById(R.id.nav_view);
        if (vistaNavegacion == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
        }
        return resultado;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_logout);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout cajon = enlace != null ? enlace.drawerLayout : null;
        if (cajon != null && cajon.isDrawerOpen(GravityCompat.START)) {
            cajon.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controladorNavegacion = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(controladorNavegacion, configuracionBarra)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (IllegalStateException e) {
        }
    }
}

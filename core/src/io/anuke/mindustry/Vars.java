package io.anuke.mindustry;

import io.anuke.arc.Application.*;
import io.anuke.arc.*;
import io.anuke.arc.assets.*;
import io.anuke.arc.files.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.util.*;
import io.anuke.mindustry.ai.*;
import io.anuke.mindustry.core.*;
import io.anuke.mindustry.entities.*;
import io.anuke.mindustry.entities.bullet.*;
import io.anuke.mindustry.entities.effect.*;
import io.anuke.mindustry.entities.impl.*;
import io.anuke.mindustry.entities.traits.*;
import io.anuke.mindustry.entities.type.*;
import io.anuke.mindustry.game.*;
import io.anuke.mindustry.gen.*;
import io.anuke.mindustry.input.*;
import io.anuke.mindustry.maps.*;
import io.anuke.mindustry.net.Net;
import io.anuke.mindustry.world.blocks.defense.ForceProjector.*;

import java.nio.charset.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class Vars implements Loadable{
    /** Whether to load locales.*/
    public static boolean loadLocales = true;
    /** IO buffer size. */
    public static final int bufferSize = 8192;
    /** global charset, since Android doesn't support the Charsets class */
    public static final Charset charset = Charset.forName("UTF-8");
    /** main application name, capitalized */
    public static final String appName = "Mindustry";
    /** URL for itch.io donations. */
    public static final String donationURL = "https://anuke.itch.io/mindustry/purchase";
    /** URL for discord invite. */
    public static final String discordURL = "https://discord.gg/mindustry";
    /** URL for sending crash reports to */
    public static final String crashReportURL = "http://mins.us.to/report";
    /** maximum distance between mine and core that supports automatic transferring */
    public static final float mineTransferRange = 220f;
    /** team of the player by default */
    public static final Team defaultTeam = Team.sharded;
    /** team of the enemy in waves/sectors */
    public static final Team waveTeam = Team.crux;
    /** whether to enable editing of units in the editor */
    public static final boolean enableUnitEditing = false;
    /** max chat message length */
    public static final int maxTextLength = 150;
    /** max player name length in bytes */
    public static final int maxNameLength = 40;
    /** displayed item size when ingame, TODO remove. */
    public static final float itemSize = 5f;
    /** extra padding around the world; units outside this bound will begin to self-destruct. */
    public static final float worldBounds = 100f;
    /** default size of UI icons.*/
    public static final int iconsize = 48;
    /** size of UI icons (small)*/
    public static final int iconsizesmall = 32;
    /** size of UI icons (medium)*/
    public static final int iconsizemed = 30;
    /** size of UI icons (medium)*/
    public static final int iconsizetiny = 16;
    /** units outside of this bound will simply die instantly */
    public static final float finalWorldBounds = worldBounds + 500;
    /** ticks spent out of bound until self destruct. */
    public static final float boundsCountdown = 60 * 7;
    /** for map generator dialog */
    public static boolean updateEditorOnChange = false;
    /** size of tiles in units */
    public static final int tilesize = 8;
    /** all choosable player colors in join/host dialog */
    public static final Color[] playerColors = {
        Color.valueOf("82759a"),
        Color.valueOf("c0c1c5"),
        Color.valueOf("fff0e7"),
        Color.valueOf("7d2953"),
        Color.valueOf("ff074e"),
        Color.valueOf("ff072a"),
        Color.valueOf("ff76a6"),
        Color.valueOf("a95238"),
        Color.valueOf("ffa108"),
        Color.valueOf("feeb2c"),
        Color.valueOf("ffcaa8"),
        Color.valueOf("008551"),
        Color.valueOf("00e339"),
        Color.valueOf("423c7b"),
        Color.valueOf("4b5ef1"),
        Color.valueOf("2cabfe"),
    };
    /** default server port */
    public static final int port = 6567;
    /** multicast discovery port.*/
    public static final int multicastPort = 20151;
    /** multicast group for discovery.*/
    public static final String multicastGroup = "227.2.7.7";
    /** if true, UI is not drawn */
    public static boolean disableUI;
    /** if true, game is set up in mobile mode, even on desktop. used for debugging */
    public static boolean testMobile;
    /** whether the game is running on a mobile device */
    public static boolean mobile;
    /** whether the game is running on an iOS device */
    public static boolean ios;
    /** whether the game is running on an Android device */
    public static boolean android;
    /** whether the game is running on a headless server */
    public static boolean headless;
    /** application data directory, equivalent to {@link io.anuke.arc.Settings#getDataDirectory()} */
    public static FileHandle dataDirectory;
    /** data subdirectory used for screenshots */
    public static FileHandle screenshotDirectory;
    /** data subdirectory used for custom mmaps */
    public static FileHandle customMapDirectory;
    /** data subdirectory used for custom mmaps */
    public static FileHandle mapPreviewDirectory;
    /** tmp subdirectory for map conversion */
    public static FileHandle tmpDirectory;
    /** data subdirectory used for saves */
    public static FileHandle saveDirectory;
    /** data subdirectory used for plugins */
    public static FileHandle pluginDirectory;
    /** old map file extension, for conversion */
    public static final String oldMapExtension = "mmap";
    /** map file extension */
    public static final String mapExtension = "msav";
    /** save file extension */
    public static final String saveExtension = "msav";

    /** list of all locales that can be switched to */
    public static Locale[] locales;

    public static ContentLoader content;
    public static GameState state;
    public static GlobalData data;
    public static EntityCollisions collisions;
    public static DefaultWaves defaultWaves;
    public static LoopControl loops;

    public static World world;
    public static Maps maps;
    public static WaveSpawner spawner;
    public static BlockIndexer indexer;
    public static Pathfinder pathfinder;

    public static Control control;
    public static Logic logic;
    public static Renderer renderer;
    public static UI ui;
    public static NetServer netServer;
    public static NetClient netClient;

    public static EntityGroup<Player> playerGroup;
    public static EntityGroup<TileEntity> tileGroup;
    public static EntityGroup<Bullet> bulletGroup;
    public static EntityGroup<EffectEntity> effectGroup;
    public static EntityGroup<DrawTrait> groundEffectGroup;
    public static EntityGroup<ShieldEntity> shieldGroup;
    public static EntityGroup<Puddle> puddleGroup;
    public static EntityGroup<Fire> fireGroup;
    public static EntityGroup<BaseUnit>[] unitGroups;

    /** all local players, currently only has one player. may be used for local co-op in the future */
    public static Player player;

    @Override
    public void loadAsync(){
        loadSettings();
        init();
    }

    public static void init(){
        Serialization.init();

        if(loadLocales){
            //load locales
            String[] stra = Core.files.internal("locales").readString().split("\n");
            locales = new Locale[stra.length];
            for(int i = 0; i < locales.length; i++){
                String code = stra[i];
                if(code.contains("_")){
                    locales[i] = new Locale(code.split("_")[0], code.split("_")[1]);
                }else{
                    locales[i] = new Locale(code);
                }
            }

            Arrays.sort(locales, Structs.comparing(l -> l.getDisplayName(l), String.CASE_INSENSITIVE_ORDER));
        }

        Version.init();

        content = new ContentLoader();
        loops = new LoopControl();
        defaultWaves = new DefaultWaves();
        collisions = new EntityCollisions();
        world = new World();

        maps = new Maps();
        spawner = new WaveSpawner();
        indexer = new BlockIndexer();
        pathfinder = new Pathfinder();

        playerGroup = Entities.addGroup(Player.class).enableMapping();
        tileGroup = Entities.addGroup(TileEntity.class, false);
        bulletGroup = Entities.addGroup(Bullet.class).enableMapping();
        effectGroup = Entities.addGroup(EffectEntity.class, false);
        groundEffectGroup = Entities.addGroup(DrawTrait.class, false);
        puddleGroup = Entities.addGroup(Puddle.class).enableMapping();
        shieldGroup = Entities.addGroup(ShieldEntity.class, false);
        fireGroup = Entities.addGroup(Fire.class).enableMapping();
        unitGroups = new EntityGroup[Team.all.length];

        for(Team team : Team.all){
            unitGroups[team.ordinal()] = Entities.addGroup(BaseUnit.class).enableMapping();
        }

        for(EntityGroup<?> group : Entities.getAllGroups()){
            group.setRemoveListener(entity -> {
                if(entity instanceof SyncTrait && Net.client()){
                    netClient.addRemovedEntity((entity).getID());
                }
            });
        }

        state = new GameState();
        data = new GlobalData();

        mobile = Core.app.getType() == ApplicationType.Android || Core.app.getType() == ApplicationType.iOS || testMobile;
        ios = Core.app.getType() == ApplicationType.iOS;
        android = Core.app.getType() == ApplicationType.Android;

        dataDirectory = Core.settings.getDataDirectory();
        screenshotDirectory = dataDirectory.child("screenshots/");
        customMapDirectory = dataDirectory.child("maps/");
        mapPreviewDirectory = dataDirectory.child("previews/");
        saveDirectory = dataDirectory.child("saves/");
        tmpDirectory = dataDirectory.child("tmp/");
        pluginDirectory = dataDirectory.child("plugins/");

        maps.load();
    }

    public static void loadSettings(){
        Core.settings.setAppName(appName);
        Core.settings.defaults("locale", "default");
        Core.keybinds.setDefaults(Binding.values());
        Core.settings.load();

        if(!loadLocales) return;

        try{
            //try loading external bundle
            FileHandle handle = Core.files.local("bundle");

            Locale locale = Locale.ENGLISH;
            Core.bundle = I18NBundle.createBundle(handle, locale);

            Log.info("NOTE: external translation bundle has been loaded.");
            if(!headless){
                Time.run(10f, () -> ui.showInfo("Note: You have successfully loaded an external translation bundle."));
            }
        }catch(Throwable e){
            //no external bundle found

            FileHandle handle = Core.files.internal("bundles/bundle");

            Locale locale;
            String loc = Core.settings.getString("locale");
            if(loc.equals("default")){
                locale = Locale.getDefault();
            }else{
                Locale lastLocale;
                if(loc.contains("_")){
                    String[] split = loc.split("_");
                    lastLocale = new Locale(split[0], split[1]);
                }else{
                    lastLocale = new Locale(loc);
                }

                locale = lastLocale;
            }

            Locale.setDefault(locale);
            Core.bundle = I18NBundle.createBundle(handle, locale);
        }
    }
}

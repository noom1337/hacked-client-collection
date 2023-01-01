package net.minecraft.client.gui;

import static org.spray.heaven.main.Initialize.alt;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;
import org.spray.heaven.font.IFont;
import org.spray.heaven.main.Heaven;
import org.spray.heaven.ui.changelog.ChangeLog;
import org.spray.heaven.ui.avx.listeners.OnClickListener;
import org.spray.heaven.ui.widgets.CheckBoxView;
import org.spray.heaven.util.file.ClientConfig;
import org.spray.heaven.util.render.shader.glow.GLSLSandboxShader;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import optifine.CustomPanorama;
import optifine.CustomPanoramaProperties;
import optifine.Reflector;

public class GuiMainMenu extends GuiScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Random RANDOM = new Random();

	/**
	 * Counts the number of screen updates.
	 */
	private final float updateCounter;

	/**
	 * The splash message.
	 */
	private String splashText;
	private GuiButton buttonResetDemo;

	/**
	 * Timer used to rotate the panorama, increases every tick.
	 */
	private float panoramaTimer;

	/**
	 * Texture allocated for the current viewport of the main menu's panorama
	 * background.
	 */
	private DynamicTexture viewportTexture;

	/**
	 * The Object object utilized as a thread lock when performing non thread-safe
	 * operations
	 */
	private final Object threadLock = new Object();
	public static final String MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here"
			+ TextFormatting.RESET + " for more information.";

	/**
	 * Width of openGLWarning2
	 */
	private int openGLWarning2Width;

	/**
	 * Width of openGLWarning1
	 */
	private int openGLWarning1Width;

	/**
	 * Left x coordinate of the OpenGL warning
	 */
	private int openGLWarningX1;

	/**
	 * Top y coordinate of the OpenGL warning
	 */
	private int openGLWarningY1;

	/**
	 * Right x coordinate of the OpenGL warning
	 */
	private int openGLWarningX2;

	/**
	 * Bottom y coordinate of the OpenGL warning
	 */
	private int openGLWarningY2;

	/**
	 * OpenGL graphics card warning.
	 */
	private String openGLWarning1;

	/**
	 * OpenGL graphics card warning.
	 */
	private String openGLWarning2;

	public static GLSLSandboxShader backgroundShader;

	/**
	 * Link to the Mojang Support about minimum requirements
	 */
	private String openGLWarningLink;
	private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
	private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation(
			"textures/gui/title/minecraft.png");
	private static final ResourceLocation field_194400_H = new ResourceLocation("textures/gui/title/edition.png");

	/**
	 * An array of all the paths to the panorama pictures.
	 */
	private static final ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[] {
			new ResourceLocation("textures/gui/title/background/panorama_0.png"),
			new ResourceLocation("textures/gui/title/background/panorama_1.png"),
			new ResourceLocation("textures/gui/title/background/panorama_2.png"),
			new ResourceLocation("textures/gui/title/background/panorama_3.png"),
			new ResourceLocation("textures/gui/title/background/panorama_4.png"),
			new ResourceLocation("textures/gui/title/background/panorama_5.png") };
	private ResourceLocation backgroundTexture;

	/**
	 * Minecraft Realms button.
	 */
	private GuiButton realmsButton;

	/**
	 * Has the check for a realms notification screen been performed?
	 */
	private boolean hasCheckedForRealmsNotification;

	public static long initTime = System.currentTimeMillis(); // Initialize as a failsafe

	/**
	 * A screen generated by realms for notifications; drawn in adition to the main
	 * menu (buttons and such from both are drawn at the same time). May be null.
	 */
	private GuiScreen realmsNotification;
	private int field_193978_M;
	private int field_193979_N;
	private GuiButton modButton;
	private GuiScreen modUpdateNotification;

	private String backgroundText;

	private CheckBoxView checkBox;
	private ChangeLog changeLog;

	public GuiMainMenu() {
		this.openGLWarning2 = MORE_INFO_TEXT;
		this.splashText = "missingno";
		IResource iresource = null;

		try {
			List<String> list = Lists.<String>newArrayList();
			iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
			BufferedReader bufferedreader = new BufferedReader(
					new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
			String s;

			while ((s = bufferedreader.readLine()) != null) {
				s = s.trim();

				if (!s.isEmpty()) {
					list.add(s);
				}
			}

			if (!list.isEmpty()) {
				while (true) {
					this.splashText = list.get(RANDOM.nextInt(list.size()));

					if (this.splashText.hashCode() != 125780783) {
						break;
					}
				}
			}
		} catch (IOException var8) {
			;
		} finally {
			IOUtils.closeQuietly((Closeable) iresource);
		}

		this.updateCounter = RANDOM.nextFloat();
		this.openGLWarning1 = "";

		if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
			this.openGLWarning1 = I18n.format("title.oldgl1");
			this.openGLWarning2 = I18n.format("title.oldgl2");
			this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}

		try {
			this.backgroundShader = new GLSLSandboxShader("gui/noise.fsh");
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load backgound shader", e);
		}
	}

	/**
	 * Is there currently a realms notification screen, and are realms notifications
	 * enabled?
	 */
	private boolean areRealmsNotificationsEnabled() {
		return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS)
				&& this.realmsNotification != null;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotification.updateScreen();
		}
		changeLog.tick();
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.viewportTexture = new DynamicTexture(256, 256);
		this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background",
				this.viewportTexture);
		this.field_193978_M = this.fontRendererObj.getStringWidth("Copyright Mojang AB. Do not distribute!");
		this.field_193979_N = this.width - this.field_193978_M - 2;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		int i = 24;
		int j = this.height / 2 - 32;

		this.addButtons(j, 22);

		synchronized (this.threadLock) {
			this.openGLWarning1Width = this.fontRendererObj.getStringWidth(this.openGLWarning1);
			this.openGLWarning2Width = this.fontRendererObj.getStringWidth(this.openGLWarning2);
			int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
			this.openGLWarningX1 = (this.width - k) / 2;
			this.openGLWarningY1 = (this.buttonList.get(0)).yPosition - 24;
			this.openGLWarningX2 = this.openGLWarningX1 + k;
			this.openGLWarningY2 = this.openGLWarningY1 + 24;
		}

		this.mc.setConnectedToRealms(false);

		initTime = System.currentTimeMillis();

		if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS)
				&& !this.hasCheckedForRealmsNotification) {
			RealmsBridge realmsbridge = new RealmsBridge();
			this.realmsNotification = realmsbridge.getNotificationScreen(this);
			this.hasCheckedForRealmsNotification = true;
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotification.setGuiSize(this.width, this.height);
			this.realmsNotification.initGui();
		}

		if (Reflector.NotificationModUpdateScreen_init.exists()) {
			this.modUpdateNotification = (GuiScreen) Reflector.call(Reflector.NotificationModUpdateScreen_init, this,
					this.modButton);
		}

		changeLog = new ChangeLog(3, 3, 250, height / 1.7f, 9);
		
		checkBox = new CheckBoxView("Background").setState(ClientConfig.GUI_BACKGROUND.isToggle());
		checkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onMouseClick(int button) {
				ClientConfig.GUI_BACKGROUND.setToggle(!ClientConfig.GUI_BACKGROUND.isToggle());
				checkBox.setState(ClientConfig.GUI_BACKGROUND.isToggle());
				ClientConfig.save();
				
			}
		});

	}

	private void addButtons(int posY, int offsetY) {
		int w = 118;
		int h = 18;
		this.buttonList.add(new GuiButton(1, this.width / 2 - (w / 2), posY, w, h, I18n.format("menu.singleplayer"))
				.disableRect().enableHeaven());
		this.buttonList
				.add(new GuiButton(2, this.width / 2 - (w / 2), posY + offsetY, w, h, I18n.format("menu.multiplayer"))
						.disableRect().enableHeaven());
		buttonList.add(new GuiButton(14, this.width / 2 - (w / 2), posY + offsetY * 2, w, h, "Alt").disableRect()
				.enableHeaven());
		buttonList.add(new GuiButton(0, this.width / 2 - (w / 2), posY + offsetY * 3, w, h, "Options").disableRect()
				.enableHeaven());
	}

	/**
	 * Adds Demo buttons on Main Menu for players who are playing Demo.
	 */
	private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
		this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo")));
		this.buttonResetDemo = this.addButton(
				new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo")));
		ISaveFormat isaveformat = this.mc.getSaveLoader();
		WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

		if (worldinfo == null) {
			this.buttonResetDemo.enabled = false;
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiWorldSelection(this));
		}

		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (button.id == 14) {
			mc.displayGuiScreen(alt);
		}

		if (button.id == 4) {
			this.mc.shutdown();
		}

		if (button.id == 6 && Reflector.GuiModList_Constructor.exists()) {
			this.mc.displayGuiScreen((GuiScreen) Reflector.newInstance(Reflector.GuiModList_Constructor, this));
		}

		if (button.id == 11) {
			this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
		}

		if (button.id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

			if (worldinfo != null) {
				this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("selectWorld.deleteQuestion"),
						"'" + worldinfo.getWorldName() + "' " + I18n.format("selectWorld.deleteWarning"),
						I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), 12));
			}
		}
	}

	private void switchToRealms() {
		RealmsBridge realmsbridge = new RealmsBridge();
		realmsbridge.switchToRealms(this);
	}

	public void confirmClicked(boolean result, int id) {
		if (result && id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			isaveformat.flushCache();
			isaveformat.deleteWorldDirectory("Demo_World");
			this.mc.displayGuiScreen(this);
		} else if (id == 12) {
			this.mc.displayGuiScreen(this);
		} else if (id == 13) {
			if (result) {
				try {
					Class<?> oclass = Class.forName("java.awt.Desktop");
					Object object = oclass.getMethod("getDesktop").invoke((Object) null);
					oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
				} catch (Throwable throwable1) {
					LOGGER.error("Couldn't open link", throwable1);
				}
			}

			this.mc.displayGuiScreen(this);
		}
	}

	/**
	 * Draws the main menu panorama
	 */
	private void drawPanorama(int mouseX, int mouseY, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		int i = 8;
		int j = 64;
		CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

		if (custompanoramaproperties != null) {
			j = custompanoramaproperties.getBlur1();
		}

		for (int k = 0; k < j; ++k) {
			GlStateManager.pushMatrix();
			float f = ((float) (k % 8) / 8.0F - 0.5F) / 64.0F;
			float f1 = ((float) (k / 8) / 8.0F - 0.5F) / 64.0F;
			float f2 = 0.0F;
			GlStateManager.translate(f, f1, 0.0F);
			GlStateManager.rotate(MathHelper.sin(this.panoramaTimer / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-this.panoramaTimer * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int l = 0; l < 6; ++l) {
				GlStateManager.pushMatrix();

				if (l == 1) {
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 3) {
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 4) {
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (l == 5) {
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				ResourceLocation[] aresourcelocation = TITLE_PANORAMA_PATHS;

				if (custompanoramaproperties != null) {
					aresourcelocation = custompanoramaproperties.getPanoramaLocations();
				}

				this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				int i1 = 255 / (k + 1);
				float f3 = 0.0F;
				bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, i1).endVertex();
				bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, i1).endVertex();
				bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, i1).endVertex();
				bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, i1).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
	}

	/**
	 * Rotate and blurs the skybox view in the main menu
	 */
	private void rotateAndBlurSkybox() {
		this.mc.getTextureManager().bindTexture(this.backgroundTexture);
		GlStateManager.glTexParameteri(3553, 10241, 9729);
		GlStateManager.glTexParameteri(3553, 10240, 9729);
		GlStateManager.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.disableAlpha();
		int i = 3;
		int j = 3;
		CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

		if (custompanoramaproperties != null) {
			j = custompanoramaproperties.getBlur2();
		}

		for (int k = 0; k < j; ++k) {
			float f = 1.0F / (float) (k + 1);
			int l = this.width;
			int i1 = this.height;
			float f1 = (float) (k - 1) / 256.0F;
			bufferbuilder.pos((double) l, (double) i1, (double) this.zLevel).tex((double) (0.0F + f1), 1.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			bufferbuilder.pos((double) l, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 1.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			bufferbuilder.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 0.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			bufferbuilder.pos(0.0D, (double) i1, (double) this.zLevel).tex((double) (0.0F + f1), 0.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
		}

		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	/**
	 * Renders the skybox in the main menu
	 */
	private void renderSkybox(int mouseX, int mouseY, float partialTicks) {
		this.mc.getFramebuffer().unbindFramebuffer();
		GlStateManager.viewport(0, 0, 256, 256);
		this.drawPanorama(mouseX, mouseY, partialTicks);
		this.rotateAndBlurSkybox();
		int i = 3;
		CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

		if (custompanoramaproperties != null) {
			i = custompanoramaproperties.getBlur3();
		}

		for (int j = 0; j < i; ++j) {
			this.rotateAndBlurSkybox();
			this.rotateAndBlurSkybox();
		}

		this.mc.getFramebuffer().bindFramebuffer(true);
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		float f2 = 120.0F / (float) (Math.max(this.width, this.height));
		float f = (float) this.height * f2 / 256.0F;
		float f1 = (float) this.width * f2 / 256.0F;
		int k = this.width;
		int l = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.0D, (double) l, (double) this.zLevel).tex((double) (0.5F - f), (double) (0.5F + f1))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		bufferbuilder.pos((double) k, (double) l, (double) this.zLevel).tex((double) (0.5F - f), (double) (0.5F - f1))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		bufferbuilder.pos((double) k, 0.0D, (double) this.zLevel).tex((double) (0.5F + f), (double) (0.5F - f1))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (0.5F + f), (double) (0.5F + f1))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.panoramaTimer += partialTicks;
		drawDefaultBackground();

		if (ClientConfig.GUI_BACKGROUND.isToggle()) {
			GlStateManager.enableAlpha();
			GlStateManager.disableCull();

			backgroundShader.useShader(this.width, this.height, mouseX, mouseY,
					(System.currentTimeMillis() - initTime) / 1000f);

			GL11.glBegin(GL11.GL_QUADS);

			GL11.glVertex2f(-1f, -1f);
			GL11.glVertex2f(-1f, 1f);
			GL11.glVertex2f(1f, 1f);
			GL11.glVertex2f(1f, -1f);

			GL11.glEnd();

			// Unbind shader
			GL20.glUseProgram(0);
		}
		// Drawable.drawRoundedRect(width / 2 - 100, height / 4 - 10, 200, 180, 20, new
		// Color(0,0,0, 220).getRGB());
		
//		int offsetY = 0;
//		for (String s : Heaven.CHANGELOG) {
//			IFont.WEB_SETTINGS.drawStringWithShadow(s, 3, 3 + offsetY, 0xFFE2E2E2);
//			offsetY += 7;
//		}
		
		changeLog.render(mouseX, mouseY);
		
		IFont.WEB_SMALL.drawString("Made with" + ChatFormatting.RED + " love " + ChatFormatting.RESET + "by TheKirkaYT", 6, height - 16, -1);
		
		checkBox.setWidth(56);
		checkBox.setHeight(14);
		checkBox.setX(width - (5 + checkBox.getWidth()));
		checkBox.setY(5);
		
		checkBox.render(mouseX, mouseY, partialTicks);


		IFont.ROBOTO_SMALL.drawHVCenteredString(Heaven.NAME + " " + Heaven.VERSION, (width >> 1), (height >> 1) - 66,
				20, -1, 1.15f);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		synchronized (this.threadLock) {
			if (!this.openGLWarning1.isEmpty() && !StringUtils.isNullOrEmpty(this.openGLWarningLink)
					&& mouseX >= this.openGLWarningX1 && mouseX <= this.openGLWarningX2
					&& mouseY >= this.openGLWarningY1 && mouseY <= this.openGLWarningY2) {
				GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
				guiconfirmopenlink.disableSecurityWarning();
				this.mc.displayGuiScreen(guiconfirmopenlink);
			}
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotification.mouseClicked(mouseX, mouseY, mouseButton);
		}

		if (mouseX > this.field_193979_N && mouseX < this.field_193979_N + this.field_193978_M
				&& mouseY > this.height - 10 && mouseY < this.height) {
			this.mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
		}
		
		checkBox.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		if (this.realmsNotification != null) {
			this.realmsNotification.onGuiClosed();
		}
	}

	private int rainbow(int delay, double speed) {
		double rainbow = Math.ceil((System.currentTimeMillis() + delay) / speed);
		rainbow %= 360.0D;
		return Color.getHSBColor((float) -((rainbow / 360.0F)), 0.9F, 0.8F).getRGB();
	}
}

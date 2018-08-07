package com.sam.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background, text, playBtn, pane;
    private Texture settingsBtn, restartBtn, settingsPane, settingsClose;
    private Texture easy, medium, hard;
    private Texture easySelected, mediumSelected, hardSelected;
    private Texture[] birds;
    private int flapState  = 0;
    private float birdY = 0, velocity, gap;

    private int level, activeLevel;
	private float[] gravity = {0, 2.5f, 3.5f, 3.5f};
	private float[] velocities = {0, -30, -40, -43};
	private int[] gaps = {0, 500, 500, 530};
    private int gameState = 0;

    private Texture topTube, bottomTube;
    private Random random;
    private float tubeVelocity;
    private float[] tubeVelocities = {0, 8, 10, 14};
    private int numberOfTubes = 4;
    private float[] tubeX = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];
    private float distanceBetweenTubes;

    private Circle birdCircle;
    private Rectangle[] topTubeRectangles;
    private Rectangle[] bottomTubeRectangles;

    private int score, highScore;
    private int scoringTube = 0;
    private BitmapFont font;

    private Texture gameOver;

    private int width , height;
    private OrthographicCamera camera;
    private Viewport viewport;

    private int settings_open = 0;

    Preferences preferences;

    public void startGame() {
        score = 0;
        level = preferences.getInteger("level");
        level = level>0?level:1;
        gap = gaps[level];
        activeLevel = level;
        scoringTube = 0;
        velocity = velocities[level];
        birdY = height/2 - birds[0].getHeight()/2;
        for(int i =0; i < numberOfTubes; i++) {
            tubeOffset[i] = (random.nextFloat() - 0.6f) * (height/2 - gap - 200);
            tubeX[i] = width/ 2 - topTube.getWidth() / 2 + width +  i * distanceBetweenTubes;
            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }
    @Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
        text = new Texture("flappy_bird.png");
		gameOver = new Texture("gameover.png");
		playBtn = new Texture("playbtn.png");
		pane = new Texture("pane.png");
        settingsBtn = new Texture("settingsbtn.png");
        restartBtn = new Texture("playbtn.png");
        settingsPane = new Texture("settingsPane.png");
        settingsClose = new Texture("settingsClose.png");

        easy = new Texture("easy.png");
        medium = new Texture("medium.png");
        hard = new Texture("hard.png");

        easySelected = new Texture("easy-selected.png");
        mediumSelected = new Texture("medium-selected.png");
        hardSelected = new Texture("hard-selected.png");

        birdCircle = new Circle();
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];
        font = new BitmapFont();
        font.setColor(Color.WHITE);
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        random = new Random();
        width = 1080;
        height = 1920;
        distanceBetweenTubes = width / 2;
        camera = new OrthographicCamera();
        viewport = new StretchViewport(width, height,camera);
        viewport.apply(true);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

        preferences = Gdx.app.getPreferences("Prefs");

        startGame();
	}

	@Override
	public void render () {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, 0, 0, width, height);
        if(gameState == 1) {
            tubeVelocity = tubeVelocities[level];
            if(tubeX[scoringTube] < (width/2 - topTube.getWidth())) {
                score++;
                if(scoringTube < numberOfTubes - 1)
                    scoringTube++;
                else
                    scoringTube = 0;
            }
            if(Gdx.input.justTouched()) {
                velocity = velocities[level];
            }
            for (int i = 0; i < numberOfTubes; i++) {
                if(tubeX[i] < -1 * topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (random.nextFloat() - 0.5f) * (height/2 - gap - 200);
                } else {
                    tubeX[i] -= tubeVelocity;
                }
                batch.draw(topTube, tubeX[i] ,
                        height / 2 + gap / 2 + tubeOffset[i] , topTube.getWidth() ,
                        topTube.getHeight() + 800);
                batch.draw(bottomTube, tubeX[i] ,
                        height / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i] - 800, bottomTube.getWidth() ,
                        bottomTube.getHeight() + 800);
                font.getData().setScale(8f);
                font.setColor(Color.WHITE);
                font.draw(batch, String.valueOf(score), 100, 200);
                topTubeRectangles[i] = new Rectangle(tubeX[i],
                        height / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i],
                        height / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i] - 800, bottomTube.getWidth(), bottomTube.getHeight() + 800);
            }
            if(birdY > 0) {
                while(birdY >= height - birds[flapState].getHeight()) {
                    birdY -= gravity[level];
                }
                velocity +=  gravity[level];
                birdY -= velocity;
                if(birdY < 0)
                    birdY = 0;
                if(birdY >= height - birds[flapState].getHeight()) {
                    birdY = height - birds[flapState].getHeight();

                }
            }
            else {
                gameState = 2;
            }
        }
        else if(gameState == 0){
            batch.draw(text, width/2 - text.getWidth()/2 ,
                    height * 2/3 - text.getHeight()/2 );
            batch.draw(playBtn, width/2 - playBtn.getWidth()/2 ,
                    height/3 - playBtn.getHeight()/2 );
            if(Gdx.input.justTouched() && settings_open != 1) {
                Vector2 vec = new Vector2(width*Gdx.input.getX()/Gdx.graphics.getWidth(),
                        height - height * Gdx.input.getY()/Gdx.graphics.getHeight());
                Rectangle startClick = new Rectangle(width/2 - playBtn.getWidth()/2,
                        height/3 - playBtn.getHeight()/2,
                        playBtn.getWidth(),
                        playBtn.getHeight());
                if(startClick.contains(vec)){
                    gameState = 1;
                }
            }
        } else if(gameState == 2) {
            if(birdY > 0) {
                velocity +=  gravity[level];
                birdY -= velocity;
                if(birdY < 0)
                    birdY = 0;
            }
            highScore = preferences.getInteger("highScore" + level);
            if(score > highScore && activeLevel == level){
                highScore = score;
                preferences.putInteger("highScore" + level, highScore);
                preferences.flush();
            }
            batch.draw(gameOver, width /2 - gameOver.getWidth(),
                    height*2/3 - gameOver.getHeight(), gameOver.getWidth() * 2,gameOver.getHeight() * 2);
            batch.draw(pane, width/2 - pane.getWidth()/2, height/2 - pane.getHeight()/2);
            GlyphLayout layout = new GlyphLayout(font, "Score: " + score);
            float fontX = width/2 - layout.width / 2;
            float fontY = height/2 + pane.getHeight()/2 - 1.5f * layout.height;
            font.getData().setScale(6f);
            if(activeLevel == 1) {
                font.setColor(0.0f, 157.0f/255, 79.0f/255, 1.0f);
            }
            else if(activeLevel == 2) {
                font.setColor(Color.ORANGE);
            }
            else {
                font.setColor(Color.RED);
            }
            font.draw(batch, "Score: " + score,  fontX, fontY);
            if(level == 1) {
                font.setColor(0.0f, 157.0f/255, 79.0f/255, 1.0f);
            }
            else if(level == 2) {
                font.setColor(Color.ORANGE);
            }
            else {
                font.setColor(Color.RED);
            }
            GlyphLayout layout1 = new GlyphLayout(font, "High Score: " + highScore);
            fontX = width/2 - layout1.width / 2;
            fontY = height/2 - pane.getHeight()/2 + 2.5f * layout1.height;
            if(layout1.width >= pane.getWidth()) {
                font.getData().setScale(5f);
                layout1 = new GlyphLayout(font, "High Score: " + highScore);
                fontX = width/2 - layout1.width / 2;
                fontY = height/2 - pane.getHeight()/2 + 2.5f * layout1.height;
            }
            font.draw(batch, "High Score: " + highScore, fontX, fontY);
            batch.draw(restartBtn, width/3 - restartBtn.getWidth() / 4,
                    height /3 - restartBtn.getHeight()/4 ,
                    restartBtn.getWidth()/2,
                    restartBtn.getHeight()/2);
            batch.draw(settingsBtn, width*2/3 - settingsBtn.getWidth() / 4,
                    height/3 - settingsBtn.getHeight()/4,
                    settingsBtn.getWidth()/2,
                    settingsBtn.getHeight()/2);

            if(Gdx.input.justTouched() && settings_open != 1) {
                Vector2 vec = new Vector2(width*Gdx.input.getX()/Gdx.graphics.getWidth(),
                        height - height * Gdx.input.getY()/Gdx.graphics.getHeight());
                Rectangle restartClick = new Rectangle(width/3 - restartBtn.getWidth() / 4,
                        height /3 - restartBtn.getHeight()/4 ,
                        restartBtn.getWidth()/2,
                        restartBtn.getHeight()/2);
                Rectangle settingsClick = new Rectangle(width*2/3 - settingsBtn.getWidth() / 4,
                        height/3 - settingsBtn.getHeight()/4,
                        settingsBtn.getWidth()/2,
                        settingsBtn.getHeight()/2);

                if(restartClick.contains(vec)) {
                    gameState = 1;
                    startGame();
                }
                else if(settingsClick.contains(vec)) {
                    settings_open = 1;
                }
            }
        }
        if(birdY > 0) {
            if(flapState == 0) {
                flapState = 1;
            }
            else {
                flapState = 0;
            }
        }

        if(gameState != 0) {
            batch.draw(birds[flapState], width/2 - birds[flapState].getWidth()/2, birdY);
        }

        birdCircle.set(width / 2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth() / 2);

        if(settings_open == 1)
            settingsMenu();

        batch.end();

        for(int i = 0; i < numberOfTubes; i++) {
            if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                // Collision
                gameState = 2;
            }
        }
    }

	public void settingsMenu() {
        Texture background = new Texture("blank.png");
        batch.draw(background, 0, 0);
        batch.draw(settingsPane, width/2 - settingsPane.getWidth()/2, height/2 - settingsPane.getHeight()/2);
        batch.draw(settingsClose, width/2 + settingsPane.getWidth()/2 - settingsClose.getWidth() - 25,
                height/2 + settingsPane.getHeight()/2 - settingsClose.getHeight() - 25,
                settingsClose.getWidth(), settingsClose.getHeight());
        Texture easyActive = easy, mediumActive = medium, hardActive = hard;
        switch (level) {
            case 1:
                easyActive = easySelected;
                break;
            case 2:
                mediumActive = mediumSelected;
                break;
            case 3:
                hardActive = hardSelected;
                break;
        }
        batch.draw(easyActive, width/2 - settingsPane.getWidth()/2 + settingsPane.getWidth()/4 - easyActive.getWidth()/2 - 50,
                height/2 - settingsPane.getHeight()/2 + settingsPane.getHeight()/3);
        batch.draw(mediumActive, width/2 - settingsPane.getWidth()/2 + settingsPane.getWidth()/2 - mediumActive.getWidth()/2,
                height/2 - settingsPane.getHeight()/2 + settingsPane.getHeight()/3);
        batch.draw(hardActive, width/2 - settingsPane.getWidth()/2 + settingsPane.getWidth()*3.0f/4 - hardActive.getWidth()/2 + 50,
                height/2 - settingsPane.getHeight()/2 + settingsPane.getHeight()/3);
        if(Gdx.input.justTouched()) {
            Vector2 vec = new Vector2(width*Gdx.input.getX()/Gdx.graphics.getWidth(),
                    height - height * Gdx.input.getY()/Gdx.graphics.getHeight());
            Rectangle closeSettings = new Rectangle(width/2 + settingsPane.getWidth()/2 - settingsClose.getWidth() - 25,
                    height/2 + settingsPane.getHeight()/2 - settingsClose.getHeight() - 25,
                    settingsClose.getWidth(), settingsClose.getHeight());
            Rectangle easyRec = new Rectangle(width/2 - settingsPane.getWidth()/2 + settingsPane.getWidth()/4 - easyActive.getWidth()/2 - 50,
                    height/2 - settingsPane.getHeight()/2 + settingsPane.getHeight()/3,
                    easyActive.getWidth(), easyActive.getHeight());
            Rectangle mediumRec = new Rectangle(width/2 - settingsPane.getWidth()/2 + settingsPane.getWidth()/2 - mediumActive.getWidth()/2,
                    height/2 - settingsPane.getHeight()/2 + settingsPane.getHeight()/3,
                    mediumActive.getWidth(), mediumActive.getHeight());
            Rectangle hardRec = new Rectangle(width/2 - settingsPane.getWidth()/2 + settingsPane.getWidth()*3.0f/4 - hardActive.getWidth()/2 + 50,
                    height/2 - settingsPane.getHeight()/2 + settingsPane.getHeight()/3,
                    hardActive.getWidth(), hardActive.getHeight());
            if(closeSettings.contains(vec)){
                settings_open = 0;
            } else if(easyRec.contains(vec)) {
                level = 1;
                settings_open = 0;
            } else if(mediumRec.contains(vec)) {
                level = 2;
                settings_open = 0;
            } else if(hardRec.contains(vec)) {
                level = 3;
                settings_open = 0;
            }
            preferences.putInteger("level", level);
            preferences.flush();
        }
    }

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}

    public void resize(int width, int height){
        viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
    }
}

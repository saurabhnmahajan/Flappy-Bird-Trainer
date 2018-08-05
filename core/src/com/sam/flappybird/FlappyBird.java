package com.sam.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background, text, playBtn, settingsBtn, restartBtn;
    private Texture[] birds;
    private int flapState  = 0;
    private float birdY = 0, velocity, gap;

    private int level;
	private float[] gravity = {0, 2.5f, 4.0f};
	private float[] velocities = {0, -32, -30};
    private int gameState = 0;

    private Texture topTube, bottomTube;
    private Random random;
    private float tubeVelocity;
    private int numberOfTubes = 4;
    private float[] tubeX = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];
    private float distanceBetweenTubes;

    private Circle birdCircle;
    private Rectangle[] topTubeRectangles;
    private Rectangle[] bottomTubeRectangles;

    private int score;
    private int scoringTube = 0;
    private BitmapFont font;

    private Texture gameOver;

    private int width , height;
    private OrthographicCamera camera;
    private Viewport viewport;

    public void startGame() {
        gap = 500f;
        score = 0;
        level = 1;
        scoringTube = 0;
        velocity = 0;
        birdY = height/2 - birds[0].getHeight()/2;
        for(int i =0; i < numberOfTubes; i++) {
            tubeOffset[i] = (random.nextFloat() - 0.6f) * (height/2 - gap - 250);
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
        settingsBtn = new Texture("settingsbtn.png");
        restartBtn = new Texture("restartbtn.png");
        birdCircle = new Circle();
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
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
        startGame();
	}

	@Override
	public void render () {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, 0, 0, width, height);
        if(gameState == 1) {
            tubeVelocity = 5 + 3 * level;
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
            if(Gdx.input.justTouched()) {
                Vector2 vec = new Vector2(width - width*Gdx.input.getX()/Gdx.graphics.getWidth(),
                        height - height * Gdx.input.getY()/Gdx.graphics.getHeight());
                Rectangle startClick = new Rectangle(width/2 - playBtn.getWidth()/2,
                        height/3 - playBtn.getHeight()/2,
                        playBtn.getWidth(),
                        playBtn.getHeight());
                Gdx.app.log(Gdx.input.getX()+ "",
                        Gdx.input.getY() + "");
                Gdx.app.log(width/2 - playBtn.getWidth()/2+ "",
                        height/3 - playBtn.getHeight()/2+ "");
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
            GlyphLayout layout = new GlyphLayout(font, "Score: " + score);
            float fontX = (float)width/2 - layout.width/2;
            float fontY = (float)height/2 - layout.height/2;
            font.draw(batch, "Score: " + score,  fontX, fontY);
            batch.draw(gameOver, width /2 - gameOver.getWidth(),
                    height/2 - gameOver.getHeight() + 200, gameOver.getWidth() * 2,gameOver.getHeight() * 2);
            batch.draw(restartBtn, width/2 - restartBtn.getWidth() / 2,
                    height/3 - restartBtn.getHeight()/2,
                    restartBtn.getWidth(),
                    restartBtn.getHeight());

            if(Gdx.input.justTouched()) {
                Vector2 vec = new Vector2(width - width*Gdx.input.getX()/Gdx.graphics.getWidth(),
                        height - height * Gdx.input.getY()/Gdx.graphics.getHeight());
                Rectangle restartClick = new Rectangle(width/2 - restartBtn.getWidth() / 2,
                        height /3 - restartBtn.getHeight()/2 ,
                        restartBtn.getWidth(),
                        restartBtn.getHeight());
                if(restartClick.contains(vec)) {
                    gameState = 1;
                    startGame();
                }
            }
        }
        if(flapState == 0) {
            flapState = 1;
        }
        else {
            flapState = 0;
        }

        if(gameState != 0) {
            batch.draw(birds[flapState], width/2 - birds[flapState].getWidth()/2, birdY);
        }

        birdCircle.set(width / 2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth() / 2);
        batch.end();

        for(int i = 0; i < numberOfTubes; i++) {
            if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                // Collision
                gameState = 2;
            }
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

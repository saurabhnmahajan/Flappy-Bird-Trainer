package com.sam.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

import sun.rmi.runtime.Log;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background, text, playBtn, settingsBtn, restartBtn;
	Texture[] birds;
	int flapState  = 0;
	float birdY = 0, velocity, gap;

	int level;
	float[] gravity = {0, 2.0f, 2.5f};
	float[] velocities = {0, -25, -30};
    int gameState = 0;

    Texture topTube;
    Texture bottomTube;
	float maxTubeOffset;
	Random random;
	float tubeVelocity;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTubes;

    Circle birdCircle;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    int score;
    int scoringTube = 0;
    BitmapFont font;

    Texture gameOver;

    public void startGame() {
        gap = 500f;
        score = 0;
        level = 1;
        scoringTube = 0;
        velocity = 0;
        birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;
        for(int i =0; i < numberOfTubes; i++) {
            tubeOffset[i] = (random.nextFloat() - 0.6f) * (Gdx.graphics.getHeight()/2 - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() +  i * distanceBetweenTubes;
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
        maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
        random = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
        Gdx.app.log("Height", Gdx.graphics.getHeight() + "");
        startGame();
	}

	@Override
	public void render () {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(gameState == 1) {
            tubeVelocity = 10f + level;
            if(tubeX[scoringTube] < (Gdx.graphics.getWidth()/2 - topTube.getWidth())) {
                score++;
                if(scoringTube < numberOfTubes - 1)
                    scoringTube++;
                else
                    scoringTube = 0;
                if(score > 20)
                    level = 2;
            }
            if(Gdx.input.justTouched()) {
                velocity = velocities[level];
            }
            for (int i = 0; i < numberOfTubes; i++) {
                if(tubeX[i] < -1 * topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()/2 - gap - 200);
                } else {
                    tubeX[i] -= tubeVelocity;
                }
                batch.draw(topTube, tubeX[i] ,
                        Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i] , topTube.getWidth() ,
                        topTube.getHeight() + 800);
                batch.draw(bottomTube, tubeX[i] ,
                        Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i] - 800, bottomTube.getWidth() ,
                        bottomTube.getHeight() + 800);
                font.draw(batch, String.valueOf(score), 100, 200);
                topTubeRectangles[i] = new Rectangle(tubeX[i],
                        Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i],
                        Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i] - 800, bottomTube.getWidth(), bottomTube.getHeight() + 800);
            }
            if(birdY > 0) {
                while(birdY >= Gdx.graphics.getHeight() - birds[flapState].getHeight()) {
                    birdY -= gravity[level];
                }
                velocity +=  gravity[level];
                birdY -= velocity;
                if(birdY < 0)
                    birdY = 0;
                if(birdY >= Gdx.graphics.getHeight() - birds[flapState].getHeight()) {
                    birdY = Gdx.graphics.getHeight() - birds[flapState].getHeight();

                }
            }
            else {
                gameState = 2;
            }
        }
        else if(gameState == 0){
            batch.draw(text, Gdx.graphics.getWidth()/2 - text.getWidth()/2 ,
                    Gdx.graphics.getHeight() * 2/3 - text.getHeight()/2 );
            batch.draw(playBtn, Gdx.graphics.getWidth()/2 - playBtn.getWidth()/2 ,
                    Gdx.graphics.getHeight()/3 - playBtn.getHeight()/2 );
            if(Gdx.input.justTouched()) {
                Vector2 vec = new Vector2(Gdx.graphics.getWidth() - Gdx.input.getX(),
                        Gdx.graphics.getHeight() - Gdx.input.getY());

                Rectangle startClick = new Rectangle(Gdx.graphics.getWidth()/2 - playBtn.getWidth()/2,
                        Gdx.graphics.getHeight()/3 - playBtn.getHeight()/2,
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
            GlyphLayout layout = new GlyphLayout(font, "Score: " + score);
            float fontX = (float)Gdx.graphics.getWidth()/2 - layout.width/2;
            float fontY = (float)Gdx.graphics.getHeight()/2 - layout.height/2;
            font.draw(batch, "Score: " + score,  fontX, fontY);
            batch.draw(gameOver, Gdx.graphics.getWidth() /2 - gameOver.getWidth(),
                    Gdx.graphics.getHeight()/2 - gameOver.getHeight() + 200, gameOver.getWidth() * 2,gameOver.getHeight() * 2);
            batch.draw(restartBtn, Gdx.graphics.getWidth()/2 - restartBtn.getWidth() / 2,
                    Gdx.graphics.getHeight() /3 - restartBtn.getHeight()/2,
                    restartBtn.getWidth(),
                    restartBtn.getHeight());
            if(Gdx.input.justTouched()) {
                Vector2 vec = new Vector2(Gdx.graphics.getWidth() - Gdx.input.getX(),
                        Gdx.graphics.getHeight() - Gdx.input.getY());
                Rectangle restartClick = new Rectangle(Gdx.graphics.getWidth()/2 - restartBtn.getWidth() / 2,
                        Gdx.graphics.getHeight() /3 - restartBtn.getHeight()/2 ,
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
            batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY);
        }

        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth() / 2);
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
}

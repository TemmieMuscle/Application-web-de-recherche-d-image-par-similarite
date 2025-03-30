import Phaser from "phaser";

class Scene extends Phaser.Scene {
    constructor(props) {
        super(props);
    }

    preload(){
        this.load.setBaseURL("http://localhost:8080");
        this.load.setPath("images/");
        this.load.image("test","0");
    }

    create(){
        this.cursors = this.input.keyboard.createCursorKeys();

        this.player = this.physics.add.image(50, 50, "test");
        this.player.setCollideWorldBounds(true);
        this.player.setScale(0.1,0.1);
        this.player.body.setGravityY(600)
    }

    update() {
        if(this.cursors.right.isDown){
            this.player.setVelocityX(160);
        }
        else if(this.cursors.left.isDown){
            this.player.setVelocityX(-160);
        }
        else {
            this.player.setVelocityX(0);
        }
        if(this.cursors.space.isDown){
            this.player.setVelocityY(-300);
        }
    }
}

export default Scene;
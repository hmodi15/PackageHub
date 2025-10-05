package onetomany;

import java.util.Date;

import onetomany.Buildings.BuildingRepository;
import onetomany.Images.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

import onetomany.Packages.Package;
import onetomany.Packages.PackageRepository;
import onetomany.Rooms.Room;
import onetomany.Rooms.RoomRepository;
import onetomany.Users.User;
import onetomany.Users.UserRepository;
import onetomany.Labels.Label_Image;
import onetomany.Labels.LabelRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(basePackages = {"onetomany"})
public class Main {
    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }


}
